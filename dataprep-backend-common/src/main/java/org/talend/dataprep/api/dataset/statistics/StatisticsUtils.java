package org.talend.dataprep.api.dataset.statistics;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.type.Type;
import org.talend.dataquality.statistics.cardinality.CardinalityStatistics;
import org.talend.dataquality.statistics.frequency.DataFrequencyStatistics;
import org.talend.dataquality.statistics.frequency.PatternFrequencyStatistics;
import org.talend.dataquality.statistics.numeric.histogram.HistogramStatistics;
import org.talend.dataquality.statistics.numeric.quantile.QuantileStatistics;
import org.talend.dataquality.statistics.numeric.summary.SummaryStatistics;
import org.talend.dataquality.statistics.quality.ValueQualityStatistics;
import org.talend.dataquality.statistics.text.TextLengthStatistics;
import org.talend.datascience.common.inference.Analyzers;

public class StatisticsUtils {

    private StatisticsUtils() {
    }

    public static void setStatistics(List<ColumnMetadata> columns, List<Analyzers.Result> results) {
        final Iterator<ColumnMetadata> columnIterator = columns.iterator();
        for (Analyzers.Result result : results) {
            final ColumnMetadata currentColumn = columnIterator.next();
            final boolean isNumeric = Type.NUMERIC.isAssignableFrom(Type.get(currentColumn.getType()));
            final boolean isString = Type.STRING.isAssignableFrom(Type.get(currentColumn.getType()));
            final Statistics statistics = currentColumn.getStatistics();
            // Value quality (empty / invalid / ...)
            if (result.exist(ValueQualityStatistics.class)) {
                final ValueQualityStatistics valueQualityStatistics = result.get(ValueQualityStatistics.class);
                statistics.setCount(valueQualityStatistics.getCount());
                statistics.setEmpty(valueQualityStatistics.getEmptyCount());
                statistics.setInvalid(valueQualityStatistics.getInvalidCount());
                statistics.setValid(valueQualityStatistics.getValidCount());
            }
            // Cardinality (distinct + duplicates)
            if (result.exist(CardinalityStatistics.class)) {
                final CardinalityStatistics cardinalityStatistics = result.get(CardinalityStatistics.class);
                statistics.setDistinctCount(cardinalityStatistics.getDistinctCount());
                statistics.setDuplicateCount(cardinalityStatistics.getDuplicateCount());
            }
            // Frequencies (data)
            if (result.exist(DataFrequencyStatistics.class)) {
                final DataFrequencyStatistics dataFrequencyStatistics = result.get(DataFrequencyStatistics.class);
                final Map<String, Long> topTerms = dataFrequencyStatistics.getTopK(15);
                if (topTerms != null) {
                    statistics.getDataFrequencies().clear();
                    topTerms.forEach((s, o) -> statistics.getDataFrequencies().add(new DataFrequency(s, o)));
                }
            }
            // Frequencies (pattern)
            if (result.exist(PatternFrequencyStatistics.class)) {
                final PatternFrequencyStatistics patternFrequencyStatistics = result.get(PatternFrequencyStatistics.class);
                final Map<String, Long> topTerms = patternFrequencyStatistics.getTopK(15);
                if (topTerms != null) {
                    statistics.getPatternFrequencies().clear();
                    topTerms.forEach((s, o) -> statistics.getPatternFrequencies().add(new PatternFrequency(s, o)));
                }
            }
            // Quantiles
            if (result.exist(QuantileStatistics.class)) {
                final QuantileStatistics quantileStatistics = result.get(QuantileStatistics.class);
                final Quantiles quantiles = statistics.getQuantiles();
                quantiles.setLowerQuantile(quantileStatistics.getLowerQuantile());
                quantiles.setMedian(quantileStatistics.getMedian());
                quantiles.setUpperQuantile(quantileStatistics.getUpperQuantile());
            }
            // Summary (min, max, mean, variance)
            if (result.exist(SummaryStatistics.class)) {
                final SummaryStatistics summaryStatistics = result.get(SummaryStatistics.class);
                statistics.setMax(summaryStatistics.getMax());
                statistics.setMin(summaryStatistics.getMin());
                statistics.setMean(summaryStatistics.getMean());
                statistics.setVariance(summaryStatistics.getVariance());
            }
            // Histogram
            if (isNumeric && result.exist(HistogramStatistics.class)) {
                final HistogramStatistics histogramStatistics = result.get(HistogramStatistics.class);
                // Build a decimal format based on most frequent pattern
                final String pattern = statistics.getPatternFrequencies().get(0).getPattern().replace('9', '#');
                final NumberFormat format;
                if (pattern.indexOf('a') < 0) {
                    format = new DecimalFormat(pattern, DecimalFormatSymbols.getInstance(Locale.ENGLISH));
                } else {
                    format = DecimalFormat.getInstance(Locale.ENGLISH);
                }
                // Set histogram ranges
                statistics.getHistogram().clear();
                histogramStatistics.getHistogram().forEach((r, v) -> {
                    final HistogramRange range = new HistogramRange();
                    if (pattern.isEmpty()) {
                        range.getRange().setMax(r.getUpper());
                        range.getRange().setMin(r.getLower());
                    } else {
                        range.getRange().setMax(new Double(format.format(r.getUpper())));
                        range.getRange().setMin(new Double(format.format(r.getLower())));
                    }
                    range.setOccurrences(v);
                    statistics.getHistogram().add(range);
                });
            }
            // Text length
            if (isString && result.exist(TextLengthStatistics.class)) {
                final TextLengthStatistics textLengthStatistics = result.get(TextLengthStatistics.class);
                final TextLengthSummary textLengthSummary = statistics.getTextLengthSummary();
                textLengthSummary.setAverageLength(textLengthStatistics.getAvgTextLength());
                textLengthSummary.setMinimalLength(textLengthStatistics.getMinTextLength());
                textLengthSummary.setMaximalLength(textLengthStatistics.getMaxTextLength());
            }
        }
    }
}
