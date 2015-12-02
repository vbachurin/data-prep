package org.talend.dataprep.quality;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Service;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.statistics.PatternFrequency;
import org.talend.dataprep.api.dataset.statistics.Statistics;
import org.talend.dataprep.api.dataset.statistics.StreamHistogramAnalyzer;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.api.type.TypeUtils;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.CommonErrorCodes;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadataUtils;
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum;
import org.talend.dataquality.semantic.recognizer.CategoryRecognizerBuilder;
import org.talend.dataquality.semantic.statistics.SemanticAnalyzer;
import org.talend.dataquality.semantic.statistics.SemanticQualityAnalyzer;
import org.talend.dataquality.standardization.index.ClassPathDirectory;
import org.talend.dataquality.statistics.cardinality.CardinalityAnalyzer;
import org.talend.dataquality.statistics.frequency.DataTypeFrequencyAnalyzer;
import org.talend.dataquality.statistics.frequency.pattern.*;
import org.talend.dataquality.statistics.numeric.histogram.HistogramColumnParameter;
import org.talend.dataquality.statistics.numeric.histogram.HistogramParameter;
import org.talend.dataquality.statistics.numeric.quantile.QuantileAnalyzer;
import org.talend.dataquality.statistics.numeric.summary.SummaryAnalyzer;
import org.talend.dataquality.statistics.quality.DataTypeQualityAnalyzer;
import org.talend.dataquality.statistics.quality.ValueQualityAnalyzer;
import org.talend.dataquality.statistics.text.TextLengthAnalyzer;
import org.talend.datascience.common.inference.Analyzer;
import org.talend.datascience.common.inference.Analyzers;
import org.talend.datascience.common.inference.ValueQualityStatistics;
import org.talend.datascience.common.inference.type.DataType;
import org.talend.datascience.common.inference.type.DataTypeAnalyzer;

/**
 * Service in charge of analyzing dataset quality.
 */
@Service
public class AnalyzerService implements DisposableBean {

    /** This class' logger. */
    public static final Logger LOGGER = LoggerFactory.getLogger(AnalyzerService.class);


    private static CategoryRecognizerBuilder newCategoryRecognizer() {
        try {
            final URI ddPath = AnalyzerService.class.getResource("/luceneIdx/dictionary").toURI(); //$NON-NLS-1$
            final URI kwPath = AnalyzerService.class.getResource("/luceneIdx/keyword").toURI(); //$NON-NLS-1$
            return CategoryRecognizerBuilder.newBuilder() //
                    .ddPath(ddPath) //
                    .kwPath(kwPath) //
                    .lucene();
        } catch (URISyntaxException e) {
            throw new TDPException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
        }
    }

    /**
     * Build a {@link ValueQualityAnalyzer analyzer} for both type and domain validation.
     *
     * @param columns The column metadata information where type and domain are extracted from.
     * @return A non-initialized but configured {@link ValueQualityAnalyzer}.
     */
    public ValueQualityAnalyzer qualityAnalyzer(List<ColumnMetadata> columns) {
        final CategoryRecognizerBuilder categoryBuilder = newCategoryRecognizer();
        final DataType.Type[] types = TypeUtils.convert(columns);
        List<String> domainList = columns.stream() //
                .map(c -> {
                    final SemanticCategoryEnum category = SemanticCategoryEnum.getCategoryById(c.getDomain().toUpperCase());
                    return category == null ? SemanticCategoryEnum.UNKNOWN.getId() : category.getId();
                }) //
                .collect(Collectors.toList());
        final String[] domains = domainList.toArray(new String[domainList.size()]);
        DataTypeQualityAnalyzer dataTypeQualityAnalyzer = new DataTypeQualityAnalyzer(types);
        columns.forEach(c -> dataTypeQualityAnalyzer.addCustomDateTimePattern(getMostUsedDatePattern(c)));
        SemanticQualityAnalyzer semanticQualityAnalyzer = new SemanticQualityAnalyzer(categoryBuilder, domains);
        return new ValueQualityAnalyzer(dataTypeQualityAnalyzer, semanticQualityAnalyzer, true);
    }

    public Analyzer<Analyzers.Result> full(List<ColumnMetadata> columns) {
        // Configure quality & semantic analysis (if column metadata information is present in stream).
        final DataType.Type[] types = TypeUtils.convert(columns);
        final CategoryRecognizerBuilder categoryBuilder = newCategoryRecognizer();
        // Set min and max for each column in histogram
        final HistogramParameter histogramParameter = new HistogramParameter();
        for (int i = 0; i < columns.size(); i++) {
            ColumnMetadata column = columns.get(i);
            final boolean isNumeric = Type.NUMERIC.isAssignableFrom(Type.get(column.getType()));
            if (isNumeric) {
                final Statistics statistics = column.getStatistics();
                final double min = statistics.getMin();
                final double max = statistics.getMax();
                if (min < max) {
                    final HistogramColumnParameter columnParameter = new HistogramColumnParameter();
                    columnParameter.setParameters(min, max, 20);
                    histogramParameter.putColumnParameter(i, columnParameter);
                }
            }

        }
        final StreamHistogramAnalyzer histogramAnalyzer = new StreamHistogramAnalyzer(types, histogramParameter);
        final SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(categoryBuilder);
        // Configure value quality analysis
        final ValueQualityAnalyzer valueQualityAnalyzer = qualityAnalyzer(columns);
        final Analyzer<Analyzers.Result> analyzer = Analyzers.with(
                // Value quality (invalid values...)
                valueQualityAnalyzer,
                // Type analysis (especially useful for new columns).
                getDataTypeAnalyzer(columns),
                // Cardinality (distinct + duplicate)
                new CardinalityAnalyzer(),
                // Frequency analysis (Pattern + data)
                new DataTypeFrequencyAnalyzer(), getPatternFrequencyAnalyzer(columns),
                // Quantile analysis
                new QuantileAnalyzer(types),
                // Summary (min, max, mean, variance)
                new SummaryAnalyzer(types),
                // Histogram
                histogramAnalyzer,
                // Text length analysis (for applicable columns)
                new TextLengthAnalyzer(),
                // Semantic analysis
                semanticAnalyzer);
        analyzer.init();
        return analyzer;
    }

    /**
     * @see AnalyzerService#getPatternFrequencyAnalyzer(List)
     */
    public AbstractPatternFrequencyAnalyzer getPatternFrequencyAnalyzer(ColumnMetadata column) {
        return getPatternFrequencyAnalyzer(Collections.singletonList(column));
    }

    /**
     * @param columns the columns to analyze.
     * @return the analyzer for the given columns.
     */
    public AbstractPatternFrequencyAnalyzer getPatternFrequencyAnalyzer(List<ColumnMetadata> columns) {

        // deal with specific date, even custom date pattern
        final DateTimePatternFrequencyAnalyzer dateTimePatternFrequencyAnalyzer = new DateTimePatternFrequencyAnalyzer();
        final List<String> mostUsedDatePatterns = getMostUsedDatePatterns(columns);
        dateTimePatternFrequencyAnalyzer.addCustomDateTimePatterns(mostUsedDatePatterns);

        // warning, the order is important
        List<AbstractPatternFrequencyAnalyzer> patternFrequencyAnalyzers = new ArrayList<>();
        patternFrequencyAnalyzers.add(new EmptyPatternFrequencyAnalyzer());
        patternFrequencyAnalyzers.add(dateTimePatternFrequencyAnalyzer);
        patternFrequencyAnalyzers.add(new LatinExtendedCharPatternFrequencyAnalyzer());

        return new CompositePatternFrequencyAnalyzer(patternFrequencyAnalyzers);
    }

    public Analyzer<Analyzers.Result> qualityAnalysis(List<ColumnMetadata> columns) {
        DataType.Type[] types = TypeUtils.convert(columns);
        // Run analysis
        final CategoryRecognizerBuilder categoryBuilder = newCategoryRecognizer();
        // Configure value quality analysis
        final ValueQualityAnalyzer valueQualityAnalyzer = qualityAnalyzer(columns);
        final Analyzer<Analyzers.Result> analyzer = Analyzers.with(valueQualityAnalyzer, //
                new SummaryAnalyzer(types), //
                new SemanticAnalyzer(categoryBuilder), //
                getDataTypeAnalyzer(columns));
        analyzer.init();
        return analyzer;
    }

    /**
     * <p>
     * Analyse the... Schema !
     * </p>
     * <ul>
     * <li>ValueQuality</li>
     * <li>Semantic</li>
     * <li>DataType</li>
     * </ul>
     * 
     * @param columns the columns to analyze.
     * @return the analyzers to perform for the schema.
     */
    public Analyzer<Analyzers.Result> schemaAnalysis(List<ColumnMetadata> columns) {
        // Run analysis
        final CategoryRecognizerBuilder categoryBuilder = newCategoryRecognizer();
        // Configure value quality analysis
        final ValueQualityAnalyzer valueQualityAnalyzer = qualityAnalyzer(columns);
        final Analyzer<Analyzers.Result> analyzer = Analyzers.with(valueQualityAnalyzer, //
                new SemanticAnalyzer(categoryBuilder), //
                getDataTypeAnalyzer(columns));
        analyzer.init();
        return analyzer;
    }

    /**
     * @see DisposableBean#destroy()
     */
    @Override
    public void destroy() throws Exception {
        LOGGER.info("Clean up analyzers...");
        final Map<String, Analyzer<ValueQualityStatistics>> cache = ActionMetadataUtils.getAnalyzerCache();
        for (Map.Entry<String, Analyzer<ValueQualityStatistics>> entry : cache.entrySet()) {
            try {
                entry.getValue().close();
            } catch (Exception e) {
                LOGGER.error("Unable to close analyzer for type '" + entry.getKey() + "'.", e);
            }
        }
        ClassPathDirectory.destroy();
        LOGGER.info("Clean up analyzers done.");
    }

    private DataTypeAnalyzer getDataTypeAnalyzer(List<ColumnMetadata> columns) {
        final List<String> mostUsedDatePatterns = getMostUsedDatePatterns(columns);
        return new DataTypeAnalyzer(mostUsedDatePatterns);
    }

    /**
     * Return the list of most used patterns for dates.
     *
     * @param columns the columns to analyze.
     * @return the list of most used patterns for dates or an empty list if there's none.
     */
    private List<String> getMostUsedDatePatterns(List<ColumnMetadata> columns) {

        List<String> patterns = new ArrayList<>(columns.size());
        for (ColumnMetadata column : columns) {
            final String pattern = getMostUsedDatePattern(column);
            if (StringUtils.isNotBlank(pattern)) {
                patterns.add(pattern);
            }
        }

        return patterns;
    }

    /**
     * In case of a date column, return the most used pattern.
     *
     * @param column the column to inspect.
     * @return the most used pattern or null if there's none.
     */
    private String getMostUsedDatePattern(ColumnMetadata column) {
        // only filter out non date columns
        if (Type.get(column.getType()) != Type.DATE) {
            return null;
        }
        final List<PatternFrequency> patternFrequencies = column.getStatistics().getPatternFrequencies();
        if (!patternFrequencies.isEmpty()) {
            patternFrequencies.sort((p1, p2) -> Long.compare(p2.getOccurrences(), p1.getOccurrences()));
            return patternFrequencies.get(0).getPattern();
        }
        return null;
    }
}
