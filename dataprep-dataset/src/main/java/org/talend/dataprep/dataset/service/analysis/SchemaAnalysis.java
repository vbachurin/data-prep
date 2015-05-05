package org.talend.dataprep.dataset.service.analysis;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;

import javax.jms.JMSException;
import javax.jms.Message;

import org.apache.spark.SparkContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.talend.dataprep.DistributedLock;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.api.dataset.json.DataSetMetadataModule;
import org.talend.dataprep.api.type.Type;
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
public class SchemaAnalysis {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchemaAnalysis.class);

    @Autowired
    DataSetMetadataRepository repository;

    @Autowired
    DataSetContentStore store;

    @Autowired
    JmsTemplate jmsTemplate;

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    SparkContext sparkContext;

    @JmsListener(destination = Destinations.SCHEMA_ANALYSIS)
    public void analyzeSchema(Message message) {
        try {
            String dataSetId = message.getStringProperty("dataset.id"); //$NON-NLS-1$
            DistributedLock datasetLock = repository.createDatasetMetadataLock(dataSetId);
            datasetLock.lock();
            try {
                StatisticsClientJson statisticsClient = new StatisticsClientJson(true, sparkContext);
                statisticsClient.setJsonRecordPath("records"); //$NON-NLS-1$
                DataSetMetadata metadata = repository.get(dataSetId);
                if (metadata != null) {
                    try {
                        LOGGER.info("Analyzing schema in dataset #{}...", dataSetId);
                        // Create a content with the expected format for the StatisticsClientJson class
                        final SimpleModule module = DataSetMetadataModule.get(true, true, store.get(metadata), applicationContext);
                        ObjectMapper mapper = new ObjectMapper();
                        mapper.registerModule(module);
                        final StringWriter content = new StringWriter();
                        mapper.writer().writeValue(content, metadata);
                        // Determine schema for the content
                        String elasticDataSchema = statisticsClient.inferSchemaInMemory(content.toString());
                        LOGGER.debug("Analysis result: {}" + elasticDataSchema);
                        // Set column types back in data set metadata
                        final Iterator<JsonNode> columns = mapper.readTree(elasticDataSchema).get("column").elements(); //$NON-NLS-1$
                        final Iterator<ColumnMetadata> schemaColumns = metadata.getRow().getColumns().iterator();
                        for (; columns.hasNext() && schemaColumns.hasNext(); ) {
                            final JsonNode column = columns.next();
                            final ColumnMetadata schemaColumn = schemaColumns.next();
                            final String typeName = column.get("suggested type").asText(); //$NON-NLS-1$
                            if (Type.BOOLEAN.getName().equals(schemaColumn.getType()) && Type.STRING.getName().equals(typeName)) {
                                LOGGER.info("Ignore incorrect detection (boolean -> string) for column {}.", schemaColumn.getId());
                            } else if (Type.has(typeName)) {
                                // Go through Type to ensure normalized type names.
                                schemaColumn.setType(Type.get(typeName).getName());
                            } else {
                                LOGGER.error("Type '{}' does not exist.", typeName);
                            }
                        }
                        if (columns.hasNext() || schemaColumns.hasNext()) {
                            // Awkward situation: analysis code and parsed content information did not find same number of columns
                            LOGGER.warn(
                                    "Column type analysis and parsed columns for #{} do not yield same number of columns (content parsed: {} / analysis: {}).",
                                    dataSetId, schemaColumns.hasNext(), columns.hasNext());
                        }
                        LOGGER.info("Analyzed schema in dataset #{}.", dataSetId);
                        metadata.getLifecycle().schemaAnalyzed(true);
                        repository.add(metadata);
                        // Asks for a in depth schema analysis (for column type information).
                        jmsTemplate.send(Destinations.QUALITY_ANALYSIS, session -> {
                            Message schemaAnalysisMessage = session.createMessage();
                            schemaAnalysisMessage.setStringProperty("dataset.id", dataSetId); //$NON-NLS-1
                            return schemaAnalysisMessage;
                        });
                    } catch (IOException e) {
                        throw Exceptions.Internal(DataSetMessages.UNABLE_TO_ANALYZE_COLUMN_TYPES, e);
                    }
                } else {
                    LOGGER.info("Unable to analyze quality of data set #{}: seems to be removed.", dataSetId);
                }
            } finally {
                datasetLock.unlock();
                message.acknowledge();
            }
        } catch (JMSException e) {
            throw Exceptions.Internal(DataSetMessages.UNEXPECTED_JMS_EXCEPTION, e);
        }
    }
}
