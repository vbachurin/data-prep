package org.talend.dataprep.dataset.service.analysis;

import static java.util.stream.StreamSupport.stream;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.jms.JMSException;
import javax.jms.Message;

import org.apache.spark.SparkContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.talend.dataprep.DistributedLock;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.dataset.exception.DataSetErrorCodes;
import org.talend.dataprep.dataset.service.Destinations;
import org.talend.dataprep.dataset.store.DataSetContentStore;
import org.talend.dataprep.dataset.store.DataSetMetadataRepository;
import org.talend.dataprep.exception.TDPException;
import org.talend.datascience.common.inference.type.TypeInferExecutor;

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

    @Autowired
    Jackson2ObjectMapperBuilder builder;

    @JmsListener(destination = Destinations.SCHEMA_ANALYSIS)
    public void analyzeSchema(Message message) {
        try {
            String dataSetId = message.getStringProperty("dataset.id"); //$NON-NLS-1$
            DistributedLock datasetLock = repository.createDatasetMetadataLock(dataSetId);
            DataSetMetadata metadata = repository.get(dataSetId);
            try {
                datasetLock.lock();
                if (metadata != null) {
                    // Schema analysis
                    try (Stream<DataSetRow> stream = store.stream(metadata)) {
                        LOGGER.info("Analyzing schema in dataset #{}...", dataSetId);
                        // Determine schema for the content (on the 20 first rows).
                        TypeInferExecutor executor = new TypeInferExecutor();
                        stream.limit(20).forEach(row -> {
                            final Map<String, Object> rowValues = row.values();
                            final List<String> strings = stream(rowValues.values().spliterator(), false) //
                                    .map(String::valueOf) //
                                    .collect(Collectors.<String>toList());
                            executor.handle(strings.toArray(new String[strings.size()]));
                        });
                        // Find the best suitable type
                        final List<Map<String, Long>> results = executor.getResults();
                        final Iterator<ColumnMetadata> columns = metadata.getRow().getColumns().iterator();
                        results.forEach(columnResult -> {
                            long max = 0;
                            String electedType = "N/A"; //$NON-NLS-1$
                            for (Map.Entry<String, Long> entry : columnResult.entrySet()) {
                                if (entry.getValue() > max) {
                                    max = entry.getValue();
                                    electedType = entry.getKey();
                                }
                            }
                            if (columns.hasNext()) {
                                columns.next().setType(electedType);
                            } else {
                                LOGGER.error("Unable to set type '" + electedType + "' to next column (no more column in dataset).");
                            }
                        });
                    }
                    // Count lines
                    try (Stream<DataSetRow> stream = store.stream(metadata)) {
                        LOGGER.info("Analyzing content size in dataset #{}...", dataSetId);
                        // Determine content size
                        metadata.getContent().setNbRecords((int) stream.count());
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
                } else {
                    LOGGER.info("Unable to analyze quality of data set #{}: seems to be removed.", dataSetId);
                }
            } catch(Exception e) {
                if (metadata != null) {
                    metadata.getLifecycle().error(true);
                    repository.add(metadata);
                }
                throw new TDPException(DataSetErrorCodes.UNABLE_TO_ANALYZE_COLUMN_TYPES, e);
            } finally {
                datasetLock.unlock();
                message.acknowledge();
            }
        } catch (JMSException e) {
            throw new TDPException(DataSetErrorCodes.UNEXPECTED_JMS_EXCEPTION, e);
        }
    }
}
