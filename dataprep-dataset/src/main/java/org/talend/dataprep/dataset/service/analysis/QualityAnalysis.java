package org.talend.dataprep.dataset.service.analysis;

import java.io.StringWriter;
import java.util.Iterator;

import javax.jms.JMSException;
import javax.jms.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.talend.dataprep.DistributedLock;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.api.dataset.Quality;
import org.talend.dataprep.api.dataset.json.DataSetMetadataModule;
import org.talend.dataprep.dataset.exception.DataSetMessages;
import org.talend.dataprep.dataset.service.Destinations;
import org.talend.dataprep.dataset.store.DataSetContentStore;
import org.talend.dataprep.dataset.store.DataSetMetadataRepository;
import org.talend.dataprep.exception.Exceptions;
import org.talend.datascience.statistics.StatisticsClientJson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

@Component
public class QualityAnalysis {

    private static final Logger LOGGER = LoggerFactory.getLogger(QualityAnalysis.class);

    @Autowired
    DataSetMetadataRepository repository;

    @Autowired
    DataSetContentStore store;

    @Autowired
    StatisticsClientJson statisticsClient;

    @Autowired
    ApplicationContext applicationContext;

    @JmsListener(destination = Destinations.QUALITY_ANALYSIS)
    public void indexDataSet(Message message) {
        try {
            String dataSetId = message.getStringProperty("dataset.id"); //$NON-NLS-1$
            DistributedLock datasetLock = repository.createDatasetMetadataLock(dataSetId);
            datasetLock.lock();
            try {
                DataSetMetadata metadata = repository.get(dataSetId);
                if (metadata != null) {
                    if (!metadata.getLifecycle().schemaAnalyzed()) {
                        LOGGER.debug("Schema information must be computed before quality analysis can be performed, ignoring message");
                        return; // no acknowledge to allow re-poll.
                    }
                    // Create a content with the expected format for the StatisticsClientJson class
                    final SimpleModule module = DataSetMetadataModule.get(true, true, store.get(metadata), applicationContext);
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.registerModule(module);
                    final StringWriter content = new StringWriter();
                    mapper.writer().writeValue(content, metadata);
                    // Determine schema for the content
                    String elasticDataSchema = statisticsClient.inferSchemaInMemory(content.toString());
                    int topKfreqTable = 5;
                    String binsOrBuckets = "2";
                    statisticsClient.setSchema(elasticDataSchema);
                    String jsonResult = statisticsClient.doStatisticsInMemory(content.toString(), topKfreqTable, binsOrBuckets);
                    LOGGER.debug("Quality results: " + jsonResult);
                    // Use result from quality analysis
                    final Iterator<JsonNode> columns = mapper.readTree(jsonResult).get("column").elements(); //$NON-NLS-1$
                    final Iterator<ColumnMetadata> schemaColumns = metadata.getRow().getColumns().iterator();
                    for (; columns.hasNext() && schemaColumns.hasNext(); ) {
                        final JsonNode column = columns.next();
                        final ColumnMetadata schemaColumn = schemaColumns.next();
                        // Get the statistics from the returned JSON
                        final JsonNode statistics = column.get("statistics"); //$NON-NLS-1$
                        final int valid = statistics.get("valid").asInt();
                        final int invalid = statistics.get("invalid").asInt();
                        final int empty = statistics.get("empty").asInt();
                        // Set it back to the data prep beans
                        final Quality quality = schemaColumn.getQuality();
                        quality.setValid(valid);
                        quality.setInvalid(invalid);
                        quality.setEmpty(empty);
                    }
                    metadata.getLifecycle().qualityAnalyzed(true);
                    repository.add(metadata);
                } else {
                    LOGGER.info("Unable to analyze quality of data set #{}: seems to be removed.", dataSetId);
                }
            } catch (Exception e) {
                throw Exceptions.Internal(DataSetMessages.UNABLE_TO_ANALYZE_DATASET_QUALITY, e);
            } finally {
                datasetLock.unlock();
                message.acknowledge();
            }
        } catch (JMSException e) {
            throw Exceptions.Internal(DataSetMessages.UNEXPECTED_JMS_EXCEPTION, e);
        }
    }
}
