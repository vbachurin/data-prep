package org.talend.dataprep.dataset.service.analysis;

import static java.util.stream.StreamSupport.stream;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.jms.JMSException;
import javax.jms.Message;

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
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.dataset.exception.DataSetErrorCodes;
import org.talend.dataprep.dataset.service.Destinations;
import org.talend.dataprep.dataset.store.DataSetContentStore;
import org.talend.dataprep.dataset.store.DataSetMetadataRepository;
import org.talend.dataprep.exception.TDPException;
import org.talend.datascience.common.inference.Analyzer;
import org.talend.datascience.common.inference.type.DataType;
import org.talend.datascience.common.inference.type.DataTypeAnalyzer;

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
    Jackson2ObjectMapperBuilder builder;

    @JmsListener(destination = Destinations.SCHEMA_ANALYSIS)
    public void analyzeSchema(Message message) {
        try {
            String dataSetId = message.getStringProperty("dataset.id"); //$NON-NLS-1$
            DistributedLock datasetLock = repository.createDatasetMetadataLock(dataSetId);
            datasetLock.lock();
            try {
                DataSetMetadata metadata = repository.get(dataSetId);
                try {
                    if (metadata != null) {
                        // Schema analysis
                        final List<DataType> columnTypes;
                        try (Stream<DataSetRow> stream = store.stream(metadata)) {
                            LOGGER.info("Analyzing schema in dataset #{}...", dataSetId);
                            // Determine schema for the content (on the 20 first rows).
                            Analyzer<DataType> analyzer = new DataTypeAnalyzer();
                            stream.limit(20).forEach(row -> {
                                final Map<String, Object> rowValues = row.values();
                                final List<String> strings = stream(rowValues.values().spliterator(), false) //
                                        .map(String::valueOf) //
                                        .collect(Collectors.<String>toList());
                                analyzer.analyze(strings.toArray(new String[strings.size()]));
                            });
                            // Find the best suitable type
                            columnTypes = analyzer.getResult();
                            final Iterator<ColumnMetadata> columns = metadata.getRow().getColumns().iterator();
                            columnTypes.forEach(columnResult -> {
                                final Type type = Type.get(columnResult.getSuggestedType().name());
                                if (columns.hasNext()) {
                                    final ColumnMetadata nextColumn = columns.next();
                                    LOGGER.debug("Column {} -> {}", nextColumn.getId(), type.getName());
                                    nextColumn.setType(type.getName());
                                } else {
                                    LOGGER.error("Unable to set type '" + type.getName() + "' to next column (no more column in dataset).");
                                }
                            });
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
                    LOGGER.error("Unable to analyse schema for dataset {}.", dataSetId, e);
                    throw new TDPException(DataSetErrorCodes.UNABLE_TO_ANALYZE_COLUMN_TYPES, e);
                }
            } finally {
                datasetLock.unlock();
                message.acknowledge();
            }
        } catch (JMSException e) {
            throw new TDPException(DataSetErrorCodes.UNEXPECTED_JMS_EXCEPTION, e);
        }
    }
}
