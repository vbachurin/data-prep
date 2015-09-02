package org.talend.dataprep.dataset.service.analysis;

import static java.util.stream.StreamSupport.stream;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.jms.JMSException;
import javax.jms.Message;

import org.apache.commons.lang.StringUtils;
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
import org.talend.dataprep.api.dataset.statistics.DataFrequency;
import org.talend.dataprep.api.dataset.statistics.Quantiles;
import org.talend.dataprep.api.dataset.statistics.Statistics;
import org.talend.dataprep.api.dataset.statistics.TextLengthSummary;
import org.talend.dataprep.api.type.TypeUtils;
import org.talend.dataprep.dataset.exception.DataSetErrorCodes;
import org.talend.dataprep.dataset.service.Destinations;
import org.talend.dataprep.dataset.store.content.ContentStoreRouter;
import org.talend.dataprep.dataset.store.metadata.DataSetMetadataRepository;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataquality.statistics.cardinality.CardinalityAnalyzer;
import org.talend.dataquality.statistics.cardinality.CardinalityStatistics;
import org.talend.dataquality.statistics.frequency.FrequencyAnalyzer;
import org.talend.dataquality.statistics.frequency.FrequencyStatistics;
import org.talend.dataquality.statistics.frequency.PatternFrequencyAnalyzer;
import org.talend.dataquality.statistics.numeric.histogram.HistogramAnalyzer;
import org.talend.dataquality.statistics.numeric.quantile.QuantileAnalyzer;
import org.talend.dataquality.statistics.numeric.quantile.QuantileStatistics;
import org.talend.dataquality.statistics.numeric.summary.SummaryAnalyzer;
import org.talend.dataquality.statistics.numeric.summary.SummaryStatistics;
import org.talend.dataquality.statistics.quality.ValueQualityAnalyzer;
import org.talend.dataquality.statistics.quality.ValueQualityStatistics;
import org.talend.dataquality.statistics.text.TextLengthAnalyzer;
import org.talend.dataquality.statistics.text.TextLengthStatistics;
import org.talend.datascience.common.inference.Analyzer;
import org.talend.datascience.common.inference.Analyzers;
import org.talend.datascience.common.inference.type.DataType;

@Component
public class StatisticsAnalysis implements AsynchronousDataSetAnalyzer {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsAnalysis.class);

    @Autowired
    DataSetMetadataRepository repository;

    @Autowired
    ContentStoreRouter store;

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    Jackson2ObjectMapperBuilder builder;

    @JmsListener(destination = Destinations.STATISTICS_ANALYSIS)
    public void analyzeQuality(Message message) {
        try {
            String dataSetId = message.getStringProperty("dataset.id"); //$NON-NLS-1$
            try {
                analyze(dataSetId);
            } finally {
                message.acknowledge();
            }
        } catch (JMSException e) {
            throw new TDPException(DataSetErrorCodes.UNEXPECTED_JMS_EXCEPTION, e);
        }
    }

    @Override
    public void analyze(String dataSetId) {
        if (StringUtils.isEmpty(dataSetId)) {
            throw new IllegalArgumentException("Data set id cannot be null or empty.");
        }
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
                    computeStatistics(metadata, stream);
                    // Tag data set quality: now analyzed
                    metadata.getLifecycle().qualityAnalyzed(true);
                    repository.add(metadata);
                } else {
                    LOGGER.info("Unable to analyze quality of data set #{}: seems to be removed.", dataSetId);
                }
            } catch (Exception e) {
                LOGGER.warn("dataset {} generates an error", dataSetId, e);
                throw new TDPException(DataSetErrorCodes.UNABLE_TO_ANALYZE_DATASET_QUALITY, e);
            }
        } finally {
            datasetLock.unlock();
        }

    }

    public void computeStatistics(DataSetMetadata metadata, Stream<DataSetRow> stream) {
        // Create a content with the expected format for the StatisticsClientJson class
        final List<ColumnMetadata> columns = metadata.getRow().getColumns();
        DataType.Type[] types = TypeUtils.convert(columns);
        final HistogramAnalyzer histogramAnalyzer = new HistogramAnalyzer(types);
        histogramAnalyzer.init(1, 20, 5);
        Analyzer[] allAnalyzers = new Analyzer[] {
                new ValueQualityAnalyzer(types),
                // Cardinality (distinct + duplicate)
                new CardinalityAnalyzer(),
                // Frequency analysis (Pattern + data)
                new FrequencyAnalyzer(),
                new PatternFrequencyAnalyzer(),
                // Quantile analysis
                new QuantileAnalyzer(types),
                // Summary (min, max, mean, variance)
                new SummaryAnalyzer(types),
                // Histogram
                // histogramAnalyzer,
                // Text length analysis (for applicable columns)
                new TextLengthAnalyzer()
        };
        final Analyzer<Analyzers.Result> analyzer = Analyzers.with(allAnalyzers);
        stream.map(row -> {
            final Map<String, Object> rowValues = row.values();
            final List<String> strings = stream(rowValues.values().spliterator(), false) //
                    .map(String::valueOf) //
                    .collect(Collectors.<String> toList());
            return strings.toArray(new String[strings.size()]);
        }).forEach(analyzer::analyze);
        analyzer.end();
        // Store results back in data set
        final Iterator<ColumnMetadata> columnIterator = columns.iterator();
        for (Analyzers.Result result : analyzer.getResult()) {
            final ColumnMetadata currentColumn = columnIterator.next();
            final Statistics statistics = currentColumn.getStatistics();
            // Already analyzed information
            final ValueQualityStatistics valueQualityStatistics = result.get(ValueQualityStatistics.class);
            if (valueQualityStatistics != null) {
                statistics.setCount(valueQualityStatistics.getCount());
                statistics.setEmpty(valueQualityStatistics.getEmptyCount());
                statistics.setInvalid(valueQualityStatistics.getInvalidCount());
                statistics.setValid(valueQualityStatistics.getValidCount());
            }
            // Cardinality (distinct + duplicates)
            final CardinalityStatistics cardinalityStatistics = result.get(CardinalityStatistics.class);
            if (cardinalityStatistics != null) {
                statistics.setDistinctCount(cardinalityStatistics.getDistinctCount());
                statistics.setDuplicateCount(cardinalityStatistics.getDuplicateCount());
            }
            // Frequencies (data)
            final FrequencyStatistics frequencyStatistics = result.get(FrequencyStatistics.class);
            if (frequencyStatistics != null) {
                final Map<String, Long> topTerms = frequencyStatistics.getTopK(5);
                if (topTerms != null) {
                    topTerms.forEach((s, o) -> statistics.getDataFrequencies().add(new DataFrequency(s, o)));
                }
            }
            // Frequencies (pattern)
            // TODO Problem here! (PatternFrequencyAnalyzer returns same stat class as FrequencyAnalyzer)
            // Quantiles
            final QuantileStatistics quantileStatistics = result.get(QuantileStatistics.class);
            if (quantileStatistics != null) {
                final Quantiles quantiles = statistics.getQuantiles();
                quantiles.setLowerQuantile(String.valueOf(quantileStatistics.getLowerQuantile()));
                quantiles.setMedian(quantiles.getMedian());
                quantiles.setMedian(quantiles.getUpperQuantile());
            }
            // Summary (min, max, mean, variance)
            final SummaryStatistics summaryStatistics = result.get(SummaryStatistics.class);
            if (summaryStatistics != null) {
                statistics.setMax(String.valueOf(summaryStatistics.getMax()));
                statistics.setMin(String.valueOf(summaryStatistics.getMin()));
                statistics.setMean(String.valueOf(summaryStatistics.getMean()));
                statistics.setVariance(String.valueOf(summaryStatistics.getVariance()));
            }
            // Histogram
            /*final HistogramStatistics histogramStatistics = result.get(HistogramStatistics.class);
            if (histogramStatistics != null) {
                histogramStatistics.getHistogram().forEach((r, v) -> statistics.getHistogram().getRanges().add(new HistogramRange())); // TODO Check values
            }*/
            // Text length
            final TextLengthStatistics textLengthStatistics = result.get(TextLengthStatistics.class);
            if (textLengthStatistics != null) {
                final TextLengthSummary textLengthSummary = statistics.getTextLengthSummary();
                textLengthSummary.setAverageLength(textLengthStatistics.getAvgTextLength());
                textLengthSummary.setMinimalLength(textLengthStatistics.getMinTextLength());
                textLengthSummary.setMaximalLength(textLengthStatistics.getMaxTextLength());
            }
        }
    }

    @Override
    public String destination() {
        return Destinations.STATISTICS_ANALYSIS;
    }
}
