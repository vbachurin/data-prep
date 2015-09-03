package org.talend.dataprep.dataset.service.analysis;

import static java.util.stream.StreamSupport.stream;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;
import org.talend.dataprep.DistributedLock;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.Quality;
import org.talend.dataprep.api.type.TypeUtils;
import org.talend.dataprep.dataset.exception.DataSetErrorCodes;
import org.talend.dataprep.dataset.store.content.ContentStoreRouter;
import org.talend.dataprep.dataset.store.metadata.DataSetMetadataRepository;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataquality.statistics.numeric.summary.SummaryAnalyzer;
import org.talend.dataquality.statistics.numeric.summary.SummaryStatistics;
import org.talend.dataquality.statistics.quality.ValueQualityAnalyzer;
import org.talend.dataquality.statistics.quality.ValueQualityStatistics;
import org.talend.datascience.common.inference.Analyzer;
import org.talend.datascience.common.inference.Analyzers;
import org.talend.datascience.common.inference.type.DataType;

@Component
public class QualityAnalysis implements SynchronousDataSetAnalyzer {

    private static final Logger LOGGER = LoggerFactory.getLogger(QualityAnalysis.class);

    @Autowired
    DataSetMetadataRepository repository;

    @Autowired
    ContentStoreRouter store;

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    Jackson2ObjectMapperBuilder builder;

    /**
     * Analyse the dataset metadata quality.
     *
     * @param dataSetId the dataset id.
     */
    @Override
    public void analyze(String dataSetId) {
        if (StringUtils.isEmpty(dataSetId)) {
            throw new IllegalArgumentException("Data set id cannot be null or empty.");
        }
        DistributedLock datasetLock = repository.createDatasetMetadataLock(dataSetId);
        datasetLock.lock();
        try {
            DataSetMetadata metadata = repository.get(dataSetId);
            if (metadata == null) {
                LOGGER.info("Unable to analyze quality of data set #{}: seems to be removed.", dataSetId);
                return;
            }
            try (Stream<DataSetRow> stream = store.stream(metadata)) {
                if (!metadata.getLifecycle().schemaAnalyzed()) {
                    LOGGER.debug("Schema information must be computed before quality analysis can be performed, ignoring message");
                    return; // no acknowledge to allow re-poll.
                }

                computeQuality(metadata, stream);

                // ... all quality is now analyzed, mark it so.
                metadata.getLifecycle().qualityAnalyzed(true);
                repository.add(metadata);
                LOGGER.info("Analyzed quality of dataset #{}.", dataSetId);
            } catch (Exception e) {
                LOGGER.warn("dataset '{}' generate an error, message: {} ", dataSetId, e.getMessage());
                throw new TDPException(DataSetErrorCodes.UNABLE_TO_ANALYZE_DATASET_QUALITY, e);
            }
        } finally {
            datasetLock.unlock();
        }
    }

    /**
     * @see SynchronousDataSetAnalyzer#order()
     */
    @Override
    public int order() {
        return 3;
    }

    /**
     * Compute the quality (count, valid, invalid and empty) of the given dataset.
     *
     * @param dataset the dataset metadata.
     * @param records the dataset records
     */
    public void computeQuality(DataSetMetadata dataset, Stream<DataSetRow> records) {
        // Compute valid / invalid / empty count, need data types for analyzer first
        DataType.Type[] types = TypeUtils.convert(dataset.getRow().getColumns());
        // Run analysis
        LOGGER.info("Analyzing quality of dataset #{}...", dataset.getId());
        final ValueQualityAnalyzer valueQualityAnalyzer = new ValueQualityAnalyzer(types);
        valueQualityAnalyzer.setStoreInvalidValues(true);
        final Analyzer<Analyzers.Result> analyzer = Analyzers.with(valueQualityAnalyzer, new SummaryAnalyzer(types));
        records.map(row -> {
            final Map<String, Object> rowValues = row.values();
            final List<String> strings = stream(rowValues.entrySet().spliterator(), false) //
                    .filter(e -> !DataSetRow.TDP_ID.equals(e.getKey())) // Don't take TDP_ID column
                    .map(Map.Entry::getValue) //
                    .map(String::valueOf) //
                    .collect(Collectors.<String> toList());
            return strings.toArray(new String[strings.size()]);
        }).forEach(analyzer::analyze);
        // Determine content size
        final List<Analyzers.Result> result = analyzer.getResult();
        final Iterator<ColumnMetadata> iterator = dataset.getRow().getColumns().iterator();
        for (Analyzers.Result analyzerResult : result) {
            final ValueQualityStatistics valueQuality = analyzerResult.get(ValueQualityStatistics.class);
            final SummaryStatistics summaryStatistics = analyzerResult.get(SummaryStatistics.class);
            if (!iterator.hasNext()) {
                LOGGER.warn("More quality information than number of columns in data set #{}.", dataset.getId());
                break;
            }
            final ColumnMetadata currentColumn = iterator.next();
            // Set column's quality (empty / invalid / ...)
            final Quality quality = currentColumn.getQuality();
            quality.setEmpty((int) valueQuality.getEmptyCount());
            quality.setValid((int) valueQuality.getValidCount());
            quality.setInvalid((int) valueQuality.getInvalidCount());
            quality.setInvalidValues(valueQuality.getInvalidValues());
            // Remember the number of records
            dataset.getContent().setNbRecords((int) valueQuality.getCount());
            // Later processes (statistics -> histogram) need min & max
            currentColumn.getStatistics().setMax(summaryStatistics.getMax());
            currentColumn.getStatistics().setMin(summaryStatistics.getMin());
            currentColumn.getStatistics().setMean(summaryStatistics.getMean());
        }
        // If there are columns remaining, warn for missing information
        while (iterator.hasNext()) {
            LOGGER.warn("No quality information returned for {} in data set #{}.", iterator.next().getId(), dataset.getId());
        }
    }
}