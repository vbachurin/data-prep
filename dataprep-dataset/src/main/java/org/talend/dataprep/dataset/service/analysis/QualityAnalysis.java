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
import org.springframework.stereotype.Component;
import org.talend.dataprep.DistributedLock;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.Quality;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.dataset.exception.DataSetErrorCodes;
import org.talend.dataprep.dataset.service.Destinations;
import org.talend.dataprep.dataset.store.DataSetContentStore;
import org.talend.dataprep.dataset.store.DataSetMetadataRepository;
import org.talend.dataprep.exception.TDPException;
import org.talend.datascience.common.inference.Analyzer;
import org.talend.datascience.common.inference.quality.ValueQuality;
import org.talend.datascience.common.inference.quality.ValueQualityAnalyzer;
import org.talend.datascience.common.inference.type.DataType;

@Component
public class QualityAnalysis {

    private static final Logger LOGGER = LoggerFactory.getLogger(QualityAnalysis.class);

    @Autowired
    DataSetMetadataRepository repository;

    @Autowired
    DataSetContentStore store;

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    Jackson2ObjectMapperBuilder builder;

    @JmsListener(destination = Destinations.QUALITY_ANALYSIS)
    public void analyzeQuality(Message message) {
        try {
            String dataSetId = message.getStringProperty("dataset.id"); //$NON-NLS-1$
            DistributedLock datasetLock = repository.createDatasetMetadataLock(dataSetId);
            datasetLock.lock();
            try {
                DataSetMetadata metadata = repository.get(dataSetId);
                try (Stream<DataSetRow> stream = store.stream(metadata)) {
                    if (metadata != null) {
                        if (!metadata.getLifecycle().schemaAnalyzed()) {
                            LOGGER.debug("Schema information must be computed before quality analysis can be performed, ignoring message");
                            return; // no acknowledge to allow re-poll.
                        }
                        // Compute valid / invalid / empty count, need data types for analyzer first
                        final List<ColumnMetadata> columns = metadata.getRow().getColumns();
                        DataType.Type[] types = new DataType.Type[columns.size()];
                        for (int i = 0; i < columns.size(); i++) {
                            switch (Type.get(columns.get(i).getType())) {
                                case ANY:
                                case STRING:
                                    types[i] = DataType.Type.STRING;
                                    break;
                                case NUMERIC:
                                    types[i] = DataType.Type.INTEGER;
                                    break;
                                case INTEGER:
                                    types[i] = DataType.Type.INTEGER;
                                    break;
                                case DOUBLE:
                                case FLOAT:
                                    types[i] = DataType.Type.DOUBLE;
                                    break;
                                case BOOLEAN:
                                    types[i] = DataType.Type.BOOLEAN;
                                    break;
                                case DATE:
                                    types[i] = DataType.Type.DATE;
                                    break;
                            }
                        }
                        // Run analysis
                        Analyzer<ValueQuality> analyzer = new ValueQualityAnalyzer(types);
                        stream.forEach(row -> {
                            final Map<String, Object> rowValues = row.values();
                            final List<String> strings = stream(rowValues.values().spliterator(), false) //
                                    .map(String::valueOf) //
                                    .collect(Collectors.<String>toList());
                            analyzer.analyze(strings.toArray(new String[strings.size()]));
                        });
                        LOGGER.info("Analyzing quality of dataset #{}...", dataSetId);
                        // Determine content size
                        final List<ValueQuality> analyzerResult = analyzer.getResult();
                        final Iterator<ColumnMetadata> iterator = metadata.getRow().getColumns().iterator();
                        for (ValueQuality valueQuality : analyzerResult) {
                            if (!iterator.hasNext()) {
                                LOGGER.warn("More quality information than number of columns in data set #{}.", dataSetId);
                                break;
                            }
                            final Quality quality = iterator.next().getQuality();
                            quality.setEmpty((int) valueQuality.getEmptyCount());
                            quality.setValid((int) valueQuality.getValidCount());
                            quality.setInvalid((int) valueQuality.getInvalidCount());
                            metadata.getContent().setNbRecords((int) valueQuality.getCount());
                        }
                        // If there are columns remaining, warn for missing information
                        while (iterator.hasNext()) {
                            LOGGER.warn("No quality information returned for {} in data set #{}.", iterator.next().getId(), dataSetId);
                        }
                        // ... all quality is now analyzed, mark it so.
                        metadata.getLifecycle().qualityAnalyzed(true);
                        repository.add(metadata);
                    } else {
                        LOGGER.info("Unable to analyze quality of data set #{}: seems to be removed.", dataSetId);
                    }
                } catch (Exception e) {
                    if (metadata != null) {
                        metadata.getLifecycle().error(true);
                        repository.add(metadata);
                    }
                    throw new TDPException(DataSetErrorCodes.UNABLE_TO_ANALYZE_DATASET_QUALITY, e);
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
