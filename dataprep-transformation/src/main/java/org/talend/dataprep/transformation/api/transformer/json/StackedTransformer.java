package org.talend.dataprep.transformation.api.transformer.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSet;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.statistics.StatisticsAdapter;
import org.talend.dataprep.quality.AnalyzerService;
import org.talend.dataprep.transformation.api.action.ActionParser;
import org.talend.dataprep.transformation.api.action.DataSetRowAction;
import org.talend.dataprep.transformation.api.action.ParsedActions;
import org.talend.dataprep.transformation.api.action.context.ActionContext;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;
import org.talend.dataprep.transformation.api.transformer.Transformer;
import org.talend.dataprep.transformation.api.transformer.TransformerWriter;
import org.talend.dataprep.transformation.api.transformer.configuration.Configuration;
import org.talend.dataprep.transformation.format.WriterRegistrationService;
import org.talend.datascience.common.inference.Analyzer;
import org.talend.datascience.common.inference.Analyzers;

@Component
public class StackedTransformer implements Transformer {

    private static final String CONTEXT_ANALYZER = "analyzer";

    private static final int ANALYSIS_BUFFER_SIZE = 20;

    private static final NullAnalyzer NULL_ANALYZER = new NullAnalyzer();

    @Autowired
    private StatisticsAdapter adapter;

    @Autowired
    private ActionParser actionParser;

    /** Service who knows about registered writers. */
    @Autowired
    private WriterRegistrationService writersService;

    /** The data-prep jackson builder. */
    @Autowired
    private Jackson2ObjectMapperBuilder builder;

    @Autowired
    private AnalyzerService analyzerService;

    private enum AnalysisStatus {
        /**
         * Status indicating transformation hasn't yet decided what is the schema of the transformed content.
         */
        SCHEMA_ANALYSIS,
        /**
         * Schema was detected (based on first {@link #ANALYSIS_BUFFER_SIZE} rows). This status indicates transformer has
         * enough information (about type & al.) to perform a full analysis of the data.
         */
        FULL_ANALYSIS
    }

    private final ThreadLocal<List<DataSetRow>> initialAnalysisBuffer = new ThreadLocal<List<DataSetRow>>() {
        @Override
        protected List<DataSetRow> initialValue() {
            return new ArrayList<>(ANALYSIS_BUFFER_SIZE + 1);
        }
    };

    /** Indicate what the current status related to Analyzer configuration. */
    private ThreadLocal<AnalysisStatus> currentAnalysisStatus = new ThreadLocal<AnalysisStatus>() {
        @Override
        protected AnalysisStatus initialValue() {
            return AnalysisStatus.SCHEMA_ANALYSIS;
        }
    };

    /**
     * Configure the analyzer for quality depending on the current tAnalysisStatus.
     *
     * @param context the transformation context.
     * @param row the current row.
     * @return the configured analyzer.
     * @see SimpleTransformer#currentAnalysisStatus
     */
    private Analyzer<Analyzers.Result> configureAnalyzer(TransformationContext context, DataSetRow row) {
        switch (currentAnalysisStatus.get()) {
            case SCHEMA_ANALYSIS:
                if (initialAnalysisBuffer.get().size() < ANALYSIS_BUFFER_SIZE) {
                    initialAnalysisBuffer.get().add(row.clone());
                    return NULL_ANALYZER; // Returns a no op (like a "> /dev/null").
                } else {
                    emptyInitialAnalysisBuffer(context, row);
                    return configureFullAnalyzer(context, row.getRowMetadata().getColumns());
                }
            case FULL_ANALYSIS:
                return configureFullAnalyzer(context, row.getRowMetadata().getColumns());
            default:
                throw new UnsupportedOperationException("Unable to process state '" + currentAnalysisStatus + "'.");
        }
    }

    /**
     * Get or create a full analyzer (with all possible analysis), columns must contain correct type information.
     *
     * @param context the transformation context to get/put the analyzer.
     * @param columns the columns to analyze.
     * @return the full analyzer for the given columns.
     */
    private Analyzer<Analyzers.Result> configureFullAnalyzer(TransformationContext context, List<ColumnMetadata> columns) {
        Analyzer<Analyzers.Result> analyzer = (Analyzer<Analyzers.Result>) context.get(CONTEXT_ANALYZER);
        if (analyzer == null) {
            analyzer = analyzerService.full(columns);
            context.put(CONTEXT_ANALYZER, analyzer);
        }
        return analyzer;
    }

    /**
     * Empty the initial buffer and perform an early schema analysis, configure a full analyzer, run full analysis on.
     *
     * @param context the transformation context.
     * @param row the current row.
     */
    private void emptyInitialAnalysisBuffer(TransformationContext context, DataSetRow row) {
        if (currentAnalysisStatus.get() == AnalysisStatus.FULL_ANALYSIS || initialAnalysisBuffer.get().isEmpty()) {
            // Got called for nothing
            return;
        }
        // Perform a first rough guess of records in reservoir
        final List<ColumnMetadata> columns = row.getRowMetadata().getColumns();
        final Analyzer<Analyzers.Result> schema = analyzerService.schemaAnalysis(columns);
        for (DataSetRow dataSetRow : initialAnalysisBuffer.get()) {
            schema.analyze(dataSetRow.order(columns).toArray(DataSetRow.SKIP_TDP_ID));
        }
        adapter.adapt(columns, schema.getResult());
        // Now configure the actual (full) analysis and don't forget to process stored records
        Analyzer<Analyzers.Result> analyzer = configureFullAnalyzer(context, columns);
        for (DataSetRow dataSetRow : initialAnalysisBuffer.get()) {
            analyzer.analyze(dataSetRow.order(columns).toArray(DataSetRow.SKIP_TDP_ID));
        }
        // Clear all stored records and set current status to FULL_ANALYSIS for further records.
        initialAnalysisBuffer.get().clear();
        currentAnalysisStatus.set(AnalysisStatus.FULL_ANALYSIS);
    }

    @Override
    public void transform(DataSet input, Configuration configuration) {
        try {
            final ParsedActions parsedActions = actionParser.parse(configuration.getActions());
            final List<DataSetRowAction> allActions = parsedActions.getRowTransformers();
            final TransformerWriter writer = writersService.getWriter(configuration.formatId(), configuration.output(), configuration.getArguments());

            TransformationContext context = new TransformationContext();
            ExtendedStream<DataSetRow> records = ExtendedStream.extend(input.getRecords())
            // Perform transformations
            .map(r -> {
                DataSetRow current = r;
                for (DataSetRowAction action : allActions) {
                    final ActionContext actionContext = context.in(action, current.getRowMetadata().clone());
                    current.setRowMetadata(actionContext.getRowMetadata());
                    current = action.apply(current, actionContext);
                }
                return current;
            })
            // Analyze content
            .map(r -> {
                if (!input.getColumns().isEmpty()) {
                    // Use analyzer (for empty values, semantic...)
                    if (!r.isDeleted()) {
                        final DataSetRow row = r.order(r.getRowMetadata().getColumns());
                        configureAnalyzer(context, row).analyze(row.toArray(DataSetRow.SKIP_TDP_ID));
                    }
                }
                return r;
            });
            // Write metadata if writer asks for it
            if (writer.requireMetadataForHeader()) {
                records = records.mapOnce(r -> writeMetadata(writer, context, r));
            }
            // Write records
            Stack<DataSetRow> processingRows = new Stack<>();
            writer.startObject();
            records.mapOnce(r -> {
                try {
                    writer.fieldName("records");
                    writer.startArray();
                } catch (IOException e) {
                    // Ignored.
                }
                return r;
            }) //
            .forEach(r -> {
                try {
                    if (!processingRows.empty()) {
                        processingRows.pop();
                    }
                    if (r.shouldWrite()) {
                        writer.write(r);
                    }
                    processingRows.push(r);
                } catch (IOException e) {
                    // Ignored.
                }
            });
            writer.endArray();
            //
            if (!writer.requireMetadataForHeader()) {
                writeMetadata(writer, context, processingRows.pop());
            }
            writer.endObject();
            writer.flush();
        } catch (IOException e) {
            // TODO Exception
        } finally {
            currentAnalysisStatus.remove();
            initialAnalysisBuffer.remove();
            // cleanup context (to make sure resources are properly closed)
            configuration.getTransformationContext().cleanup();
        }
    }

    private DataSetRow writeMetadata(TransformerWriter writer, TransformationContext context, DataSetRow row) {
        try {
            emptyInitialAnalysisBuffer(context, row); // Call in case row number < ANALYSIS_BUFFER_SIZE
            final Analyzer<Analyzers.Result> analyzer = (Analyzer<Analyzers.Result>) context.get(CONTEXT_ANALYZER);
            if (analyzer != null) {
                // Analyzer may not be initialized when all rows were deleted.
                analyzer.end();
                adapter.adapt(row.getRowMetadata().getColumns(), analyzer.getResult());
            }
            writer.fieldName("columns");
            writer.write(row.getRowMetadata());
        } catch (IOException e) {
            // Ignored.
        }
        return row;
    }

    @Override
    public boolean accept(Configuration configuration) {
        return Configuration.class.equals(configuration.getClass()) && configuration.volume() == Configuration.Volume.SMALL;
    }

    private static class NullAnalyzer implements Analyzer<Analyzers.Result> {
        @Override
        public void init() {
            // Nothing to do
        }

        @Override

        public boolean analyze(String... strings) {
            // Nothing to do
            return true;
        }

        @Override
        public void end() {
            // Nothing to do
        }

        @Override
        public List<Analyzers.Result> getResult() {
            return Collections.emptyList();
        }

        @Override
        public Analyzer<Analyzers.Result> merge(Analyzer<Analyzers.Result> analyzer) {
            return this;
        }

        @Override
        public void close() throws Exception {
            // Nothing to do
        }
    }
}
