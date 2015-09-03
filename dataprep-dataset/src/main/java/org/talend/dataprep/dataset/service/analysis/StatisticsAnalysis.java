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
import org.talend.dataprep.api.dataset.statistics.*;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.api.type.TypeUtils;
import org.talend.dataprep.dataset.exception.DataSetErrorCodes;
import org.talend.dataprep.dataset.service.Destinations;
import org.talend.dataprep.dataset.store.content.ContentStoreRouter;
import org.talend.dataprep.dataset.store.metadata.DataSetMetadataRepository;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataquality.statistics.cardinality.CardinalityAnalyzer;
import org.talend.dataquality.statistics.cardinality.CardinalityStatistics;
import org.talend.dataquality.statistics.frequency.DataFrequencyAnalyzer;
import org.talend.dataquality.statistics.frequency.DataFrequencyStatistics;
import org.talend.dataquality.statistics.frequency.PatternFrequencyAnalyzer;
import org.talend.dataquality.statistics.frequency.PatternFrequencyStatistics;
import org.talend.dataquality.statistics.numeric.histogram.HistogramAnalyzer;
import org.talend.dataquality.statistics.numeric.histogram.HistogramStatistics;
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
                e.printStackTrace();
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
        // Find global min and max for histogram
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        for (ColumnMetadata column : columns) {
            final Statistics statistics = column.getStatistics();
            if (statistics.getMax() > max) {
                max = statistics.getMax();
            }
            if (statistics.getMin() < min) {
                min = statistics.getMin();
            }
        }
        histogramAnalyzer.init(min, max, 8);
        // Select all analysis
        Analyzer[] allAnalyzers = new Analyzer[] {
                new ValueQualityAnalyzer(types),
                // Cardinality (distinct + duplicate)
                new CardinalityAnalyzer(),
                // Frequency analysis (Pattern + data)
                new DataFrequencyAnalyzer(),
                new PatternFrequencyAnalyzer(),
                // Quantile analysis
                new QuantileAnalyzer(types),
                // Summary (min, max, mean, variance)
                new SummaryAnalyzer(types),
                // Histogram
                histogramAnalyzer,
                // Text length analysis (for applicable columns)
                new TextLengthAnalyzer()
        };
        final Analyzer<Analyzers.Result> analyzer = Analyzers.with(allAnalyzers);
        stream.map(row -> {
            final Map<String, Object> rowValues = row.values();
            final List<String> strings = stream(rowValues.entrySet().spliterator(), false) //
                    .filter(e -> !DataSetRow.TDP_ID.equals(e.getKey())) // Don't take TDP_ID column
                    .map(Map.Entry::getValue) //
                    .map(String::valueOf) //
                    .collect(Collectors.<String> toList());
            return strings.toArray(new String[strings.size()]);
        }).forEach(analyzer::analyze);
        analyzer.end();
        // Store results back in data set
        final Iterator<ColumnMetadata> columnIterator = columns.iterator();
        for (Analyzers.Result result : analyzer.getResult()) {
            final ColumnMetadata currentColumn = columnIterator.next();
            final boolean isNumeric = Type.NUMERIC.isAssignableFrom(Type.get(currentColumn.getType()));
            final Statistics statistics = currentColumn.getStatistics();
            // Value quality (empty / invalid / ...)
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
            final DataFrequencyStatistics dataFrequencyStatistics = result.get(DataFrequencyStatistics.class);
            if (dataFrequencyStatistics != null) {
                final Map<String, Long> topTerms = dataFrequencyStatistics.getTopK(5);
                if (topTerms != null) {
                    topTerms.forEach((s, o) -> statistics.getDataFrequencies().add(new DataFrequency(s, o)));
                }
            }
            // Frequencies (pattern)
            final PatternFrequencyStatistics patternFrequencyStatistics = result.get(PatternFrequencyStatistics.class);
            if (patternFrequencyStatistics != null) {
                final Map<String, Long> topTerms = patternFrequencyStatistics.getTopK(5);
                if (topTerms != null) {
                    topTerms.forEach((s, o) -> statistics.getPatternFrequencies().add(new PatternFrequency(s, o)));
                }
            }
            // Quantiles
            final QuantileStatistics quantileStatistics = result.get(QuantileStatistics.class);
            if (quantileStatistics != null && isNumeric) {
                final Quantiles quantiles = statistics.getQuantiles();
                quantiles.setLowerQuantile(quantileStatistics.getLowerQuantile());
                quantiles.setMedian(quantileStatistics.getMedian());
                quantiles.setMedian(quantileStatistics.getUpperQuantile());
            }
            // Summary (min, max, mean, variance)
            final SummaryStatistics summaryStatistics = result.get(SummaryStatistics.class);
            if (summaryStatistics != null) {
                statistics.setMax(summaryStatistics.getMax());
                statistics.setMin(summaryStatistics.getMin());
                statistics.setMean(summaryStatistics.getMean());
                statistics.setVariance(summaryStatistics.getVariance());
            }
            // Histogram
            final HistogramStatistics histogramStatistics = result.get(HistogramStatistics.class);
            if (histogramStatistics != null && isNumeric) {
                histogramStatistics.getHistogram().forEach((r, v) -> {
                    final HistogramRange range = new HistogramRange();
                    range.getRange().setMax(r.getUpper());
                    range.getRange().setMin(r.getLower());
                    range.setOccurrences(v);
                    statistics.getHistogram().add(range);
                });
            }
            // Text length
            final TextLengthStatistics textLengthStatistics = result.get(TextLengthStatistics.class);
            if (textLengthStatistics != null && !isNumeric) {
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
