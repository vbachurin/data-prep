package org.talend.dataprep.dataset.service.analysis;

import static java.util.stream.StreamSupport.stream;

import java.net.URI;
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
import org.talend.dataquality.semantic.recognizer.CategoryRecognizerBuilder;
import org.talend.datascience.common.inference.Analyzer;
import org.talend.datascience.common.inference.Analyzers;
import org.talend.datascience.common.inference.semantic.SemanticAnalyzer;
import org.talend.datascience.common.inference.semantic.SemanticType;
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
                analyseMetadata(dataSetId, metadata);
            } finally {
                datasetLock.unlock();
                message.acknowledge();
            }
        } catch (JMSException e) {
            throw new TDPException(DataSetErrorCodes.UNEXPECTED_JMS_EXCEPTION, e);
        }
    }

    /**
     * Analyse the dataset metadata.
     *
     * @param dataSetId the dataset id.
     * @param metadata the dataset metadata to analyse.
     */
    private void analyseMetadata(String dataSetId, DataSetMetadata metadata) {

        if (metadata == null) {
            LOGGER.info("Unable to analyze quality of data set #{}: seems to be removed.", dataSetId);
            return;
        }

        try {
            // Schema analysis
            try (Stream<DataSetRow> stream = store.stream(metadata)) {
                LOGGER.info("Analyzing schema in dataset #{}...", dataSetId);
                // Configure analyzers
                final URI ddPath = this.getClass().getResource("/luceneIdx/dictionary").toURI(); //$NON-NLS-1$
                final URI kwPath = this.getClass().getResource("/luceneIdx/keyword").toURI(); //$NON-NLS-1$
                final CategoryRecognizerBuilder builder = CategoryRecognizerBuilder.newBuilder() //
                        .ddPath(ddPath) //
                        .kwPath(kwPath) //
                        .setMode(CategoryRecognizerBuilder.Mode.LUCENE);
                final DataTypeAnalyzer dataTypeAnalyzer = new DataTypeAnalyzer();
                final SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(builder);
                final Analyzer<Analyzers.Result> analyzer = Analyzers.with(dataTypeAnalyzer, semanticAnalyzer);
                // Determine schema for the content (on the 20 first rows).
                stream.limit(20).map(row -> {
                    final Map<String, Object> rowValues = row.values();
                    final List<String> strings = stream(rowValues.values().spliterator(), false) //
                            .map(String::valueOf) //
                            .collect(Collectors.<String> toList());
                    return strings.toArray(new String[strings.size()]);
                }).forEach(analyzer::analyze);
                // Find the best suitable type
                List<Analyzers.Result> columnTypes = analyzer.getResult();
                final Iterator<ColumnMetadata> columns = metadata.getRow().getColumns().iterator();
                columnTypes.forEach(columnResult -> {
                    // Column data type
                    final DataType dataType = columnResult.get(DataType.class);
                    final Type type = Type.get(dataType.getSuggestedType().name());
                    // Semantic type
                    final SemanticType semanticType = columnResult.get(SemanticType.class);
                    if (columns.hasNext()) {
                        final ColumnMetadata nextColumn = columns.next();
                        LOGGER.debug("Column {} -> {}", nextColumn.getId(), type.getName());
                        nextColumn.setType(type.getName());
                        nextColumn.setDomain(semanticType.getSuggestedCategory());
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

        } catch (Exception e) {
            if (metadata != null) {
                metadata.getLifecycle().error(true);
                repository.add(metadata);
            }
            LOGGER.error("Unable to analyse schema for dataset " + dataSetId + ".", e);
            throw new TDPException(DataSetErrorCodes.UNABLE_TO_ANALYZE_COLUMN_TYPES, e);
        }
    }
}