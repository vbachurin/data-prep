package org.talend.dataprep.transformation.api.transformer.json;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
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
import org.talend.dataprep.transformation.api.action.metadata.SchemaChangeAction;
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

    public static final String CONTEXT_ANALYZER = "analyzer";

    @Autowired
    ActionParser actionParser;

    /** The data-prep jackson builder. */
    @Autowired
    private Jackson2ObjectMapperBuilder builder;

    private static Analyzer<Analyzers.Result> configureAnalyzer(TransformationContext context) {
        try {
            Analyzer<Analyzers.Result> analyzer = (Analyzer<Analyzers.Result>) context.get(CONTEXT_ANALYZER);
            if (analyzer == null) {
                // Configure quality & semantic analysis (if column metadata information is present in stream).
                final List<ColumnMetadata> columns = context.getTransformedRowMetadata().getColumns();
                final DataType.Type[] types = TypeUtils.convert(columns);
                final URI ddPath = SimpleTransformer.class.getResource("/luceneIdx/dictionary").toURI(); //$NON-NLS-1$
                final URI kwPath = SimpleTransformer.class.getResource("/luceneIdx/keyword").toURI(); //$NON-NLS-1$
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
                analyzer = Analyzers.with(new ValueQualityAnalyzer(types),
                        // Cardinality (distinct + duplicate)
                        new CardinalityAnalyzer(),
                        // Frequency analysis (Pattern + data)
                        new DataFrequencyAnalyzer(), new PatternFrequencyAnalyzer(),
                        // Quantile analysis
                        new QuantileAnalyzer(types),
                        // Summary (min, max, mean, variance)
                        new SummaryAnalyzer(types),
                        // Histogram
                        histogramAnalyzer,
                        // Text length analysis (for applicable columns)
                        new TextLengthAnalyzer(), new SemanticAnalyzer(categoryBuilder));
                context.put(CONTEXT_ANALYZER, analyzer);
            }
            return analyzer;
        } catch (URISyntaxException e) {
            throw new TDPException(TransformationErrorCodes.UNABLE_TRANSFORM_DATASET, e);
        }
    }

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
            final AtomicBoolean wroteMetadata = new AtomicBoolean(false);
            // Row transformations
            Stream<DataSetRow> records = input.getRecords();
            writer.fieldName("records");
            writer.startArray();
            // Apply actions to records
            for (DataSetRowAction action : rowActions) {
                records = records.map(r -> action.apply(r, context));
            }
            records = records.map(r -> {
                //
                context.setTransformedRowMetadata(r.getRowMetadata());
                // Configure analyzer
                if (transformColumns) {
                    // Use analyzer (for empty values, semantic...)
                    if (!r.isDeleted()) {
                        final DataSetRow row = r.order(r.getRowMetadata().getColumns());
                        configureAnalyzer(context).analyze(row.toArray(DataSetRow.SKIP_TDP_ID));
                    }
                }
                return r;
            });
            // Write transformed records to stream
            records.forEach(row -> {
                try {
                    if (writer.requireMetadataForHeader() && !wroteMetadata.get()) {
                        writer.write(row.getRowMetadata());
                        wroteMetadata.set(true);
                    }
                    if (row.shouldWrite()) {
                        writer.write(row);
                        context.freezeActionContexts();
                    }
                } catch (IOException e) {
                    throw new TDPException(TransformationErrorCodes.UNABLE_TRANSFORM_DATASET, e);
                }
            });
            writer.endArray();
            // Write columns
            if (!wroteMetadata.get() && transformColumns) {
                Set<String> forcedColumns = (Set<String>) context.get(SchemaChangeAction.FORCED_TYPE_SET_CTX_KEY);
                if (forcedColumns == null) {
                    forcedColumns = Collections.emptySet();
                }
                writer.fieldName("columns");
                final RowMetadata row = context.getTransformedRowMetadata();
                final Analyzer<Analyzers.Result> analyzer = (Analyzer<Analyzers.Result>) context.get(CONTEXT_ANALYZER);
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
                    // we do not change again the domain as it has been maybe override by the user
                    if (!(metadata.isDomainForced() || metadata.isTypeForced()) && !forcedColumns.contains(metadata.getId())) {
                        // Semantic types
                        final SemanticType semanticType = result.get(SemanticType.class);
                        metadata.setDomain(TypeUtils.getDomainLabel(semanticType));
                    }
                }
                // Set the statistics
                analyzer.end(); // TODO SemanticAnalyzer is erased on end(), call end after metadata is set (wait for TDQ-10970).
                StatisticsUtils.setStatistics(row.getColumns(), analyzer.getResult());
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
