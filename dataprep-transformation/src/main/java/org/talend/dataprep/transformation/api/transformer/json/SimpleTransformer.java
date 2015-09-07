package org.talend.dataprep.transformation.api.transformer.json;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.*;
import org.talend.dataprep.api.dataset.statistics.Statistics;
import org.talend.dataprep.api.dataset.statistics.StatisticsUtils;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.api.type.TypeUtils;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.transformation.api.action.ActionParser;
import org.talend.dataprep.transformation.api.action.DataSetRowAction;
import org.talend.dataprep.transformation.api.action.ParsedActions;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;
import org.talend.dataprep.transformation.api.transformer.Transformer;
import org.talend.dataprep.transformation.api.transformer.TransformerWriter;
import org.talend.dataprep.transformation.api.transformer.configuration.Configuration;
import org.talend.dataprep.transformation.exception.TransformationErrorCodes;
import org.talend.dataquality.semantic.recognizer.CategoryRecognizerBuilder;
import org.talend.dataquality.statistics.cardinality.CardinalityAnalyzer;
import org.talend.dataquality.statistics.frequency.DataFrequencyAnalyzer;
import org.talend.dataquality.statistics.frequency.PatternFrequencyAnalyzer;
import org.talend.dataquality.statistics.numeric.histogram.HistogramAnalyzer;
import org.talend.dataquality.statistics.numeric.quantile.QuantileAnalyzer;
import org.talend.dataquality.statistics.numeric.summary.SummaryAnalyzer;
import org.talend.dataquality.statistics.quality.ValueQualityAnalyzer;
import org.talend.dataquality.statistics.quality.ValueQualityStatistics;
import org.talend.dataquality.statistics.text.TextLengthAnalyzer;
import org.talend.datascience.common.inference.Analyzer;
import org.talend.datascience.common.inference.Analyzers;
import org.talend.datascience.common.inference.semantic.SemanticAnalyzer;
import org.talend.datascience.common.inference.semantic.SemanticType;
import org.talend.datascience.common.inference.type.DataType;

/**
 * Base implementation of the Transformer interface.
 */
@Component
class SimpleTransformer implements Transformer {

    @Autowired
    ActionParser actionParser;

    /** The data-prep jackson builder. */
    @Autowired
    private Jackson2ObjectMapperBuilder builder;

    /**
     * @see Transformer#transform(DataSet, Configuration)
     */
    @Override
    public void transform(DataSet input, Configuration configuration) {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null.");
        }
        final TransformerWriter writer = configuration.writer();
        try {
            writer.startObject();
            final ParsedActions parsedActions = actionParser.parse(configuration.getActions());
            final List<DataSetRowAction> rowActions = parsedActions.getRowTransformers();
            final boolean transformColumns = !input.getColumns().isEmpty();
            TransformationContext context = configuration.getTransformationContext();
            // Row transformations
            Stream<DataSetRow> records = input.getRecords();
            writer.fieldName("records");
            writer.startArray();
            // Apply actions to records
            for (DataSetRowAction action : rowActions) {
                records = records.map(r -> action.apply(r.clone(), context));
            }
            records = records.map(r -> {
                //
                context.setTransformedRowMetadata(r.getRowMetadata());
                // Configure analyzer
                if (transformColumns) {
                    Analyzer<Analyzers.Result> analyzer = null;
                    try {
                        analyzer = (Analyzer<Analyzers.Result>) context.get("analyzer");
                        if (analyzer == null) {
                            // Configure quality & semantic analysis (if column metadata information is present in stream).
                            final List<ColumnMetadata> columns = context.getTransformedRowMetadata().getColumns();
                            final DataType.Type[] types = TypeUtils.convert(columns);
                            final URI ddPath = this.getClass().getResource("/luceneIdx/dictionary").toURI(); //$NON-NLS-1$
                            final URI kwPath = this.getClass().getResource("/luceneIdx/keyword").toURI(); //$NON-NLS-1$
                            final CategoryRecognizerBuilder categoryBuilder = CategoryRecognizerBuilder.newBuilder() //
                                    .ddPath(ddPath) //
                                    .kwPath(kwPath) //
                                    .setMode(CategoryRecognizerBuilder.Mode.LUCENE);
                            // Find global min and max for histogram
                            double min = Double.MAX_VALUE;
                            double max = Double.MIN_VALUE;
                            boolean hasMetNumeric = false;
                            for (ColumnMetadata column : columns) {
                                final boolean isNumeric = Type.NUMERIC.isAssignableFrom(Type.get(column.getType()));
                                if (isNumeric) {
                                    final Statistics statistics = column.getStatistics();
                                    if (statistics.getMax() > max) {
                                        max = statistics.getMax();
                                    }
                                    if (statistics.getMin() < min) {
                                        min = statistics.getMin();
                                    }
                                    hasMetNumeric = true;
                                }
                            }
                            final HistogramAnalyzer histogramAnalyzer = new HistogramAnalyzer(types);
                            if (hasMetNumeric) {
                                histogramAnalyzer.init(min, max, 8);
                            }
                            analyzer = Analyzers.with( new ValueQualityAnalyzer(types),
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
                                    new TextLengthAnalyzer(),
                                    new SemanticAnalyzer(categoryBuilder));
                            context.put("analyzer", analyzer);
                        }
                    } catch (URISyntaxException e) {
                        throw new TDPException(TransformationErrorCodes.UNABLE_TRANSFORM_DATASET, e);
                    }
                    // Use analyzer (for empty values, semantic...)
                    if (!r.isDeleted()) {
                        analyzer.analyze(r.toArray());
                    }
                }
                return r;
            });
            // Write transformed records to stream
            AtomicBoolean wroteMetadata = new AtomicBoolean(false);
            records.forEach(row -> {
                if (writer.requireMetadataForHeader() && !wroteMetadata.get()) {
                    try {
                        writer.write(row.getRowMetadata());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } finally {
                        wroteMetadata.set(true);
                    }
                }
                try {
                    if (row.shouldWrite()) {
                        writer.write(row);
                    }
                } catch (IOException e) {
                    throw new TDPException(TransformationErrorCodes.UNABLE_TRANSFORM_DATASET, e);
                }
            });
            writer.endArray();
            // Write columns
            if (!wroteMetadata.get() && transformColumns) {
                writer.fieldName("columns");
                final RowMetadata row = context.getTransformedRowMetadata();
                final Analyzer<Analyzers.Result> analyzer = (Analyzer<Analyzers.Result>) context.get("analyzer");
                analyzer.end();
                // Set metadata information (not in statistics).
                final List<ColumnMetadata> dataSetColumns = row.getColumns();
                final List<Analyzers.Result> results = analyzer.getResult();
                for (int i = 0; i < results.size(); i++) {
                    final Analyzers.Result result = results.get(i);
                    final ColumnMetadata metadata = dataSetColumns.get(i);
                    // Value quality
                    final ValueQualityStatistics column = result.get(ValueQualityStatistics.class);
                    final Quality quality = metadata.getQuality();
                    quality.setEmpty((int) column.getEmptyCount());
                    quality.setInvalid((int) column.getInvalidCount());
                    quality.setValid((int) column.getValidCount());
                    quality.setInvalidValues(column.getInvalidValues());
                    // Semantic types
                    final SemanticType semanticType = result.get(SemanticType.class);
                    metadata.setDomain(TypeUtils.getDomainLabel(semanticType));
                }
                // Set the statistics
                StatisticsUtils.setStatistics(row.getColumns(), analyzer);
                writer.write(context.getTransformedRowMetadata());
            }
            writer.endObject();
            writer.flush();
        } catch (IOException e) {
            throw new TDPException(TransformationErrorCodes.UNABLE_TRANSFORM_DATASET, e);
        }
    }

    @Override
    public boolean accept(Configuration configuration) {
        return Configuration.class.equals(configuration.getClass()) && configuration.volume() == Configuration.Volume.SMALL;
    }

}
