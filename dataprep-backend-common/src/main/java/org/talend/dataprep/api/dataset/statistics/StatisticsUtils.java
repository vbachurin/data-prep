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
                final Map<String, Long> topTerms = dataFrequencyStatistics.getTopK(15);
                if (topTerms != null) {
                    statistics.getDataFrequencies().clear();
                    topTerms.forEach((s, o) -> statistics.getDataFrequencies().add(new DataFrequency(s, o)));
                }
            }
            // Frequencies (pattern)
            final PatternFrequencyStatistics patternFrequencyStatistics = result.get(PatternFrequencyStatistics.class);
            if (patternFrequencyStatistics != null) {
                final Map<String, Long> topTerms = patternFrequencyStatistics.getTopK(15);
                if (topTerms != null) {
                    statistics.getPatternFrequencies().clear();
                    topTerms.forEach((s, o) -> statistics.getPatternFrequencies().add(new PatternFrequency(s, o)));
                }
            }
            // Quantiles
            final QuantileStatistics quantileStatistics = result.get(QuantileStatistics.class);
            if (quantileStatistics != null && isNumeric) {
                final Quantiles quantiles = statistics.getQuantiles();
                quantiles.setLowerQuantile(quantileStatistics.getLowerQuantile());
                quantiles.setMedian(quantileStatistics.getMedian());
                quantiles.setUpperQuantile(quantileStatistics.getUpperQuantile());
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
                    range.getRange().setMax(new Double(format.format(r.getUpper())));
                    range.getRange().setMin(new Double(format.format(r.getLower())));
                    range.setOccurrences(v);
                    statistics.getHistogram().add(range);
                });
            }
            // Text length
            final TextLengthStatistics textLengthStatistics = result.get(TextLengthStatistics.class);
            if (textLengthStatistics != null && isString) {
                final TextLengthSummary textLengthSummary = statistics.getTextLengthSummary();
                textLengthSummary.setAverageLength(textLengthStatistics.getAvgTextLength());
                textLengthSummary.setMinimalLength(textLengthStatistics.getMinTextLength());
                textLengthSummary.setMaximalLength(textLengthStatistics.getMaxTextLength());
            }
        }
    }
}
