package org.talend.dataprep.transformation.api.transformer.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSet;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.statistics.StatisticsAdapter;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.TransformationErrorCodes;
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

/**
 * Base implementation of the Transformer interface.
 */
// @Component
class SimpleTransformer implements Transformer {

    private static final String CONTEXT_ANALYZER = "analyzer";

    private static final int ANALYSIS_BUFFER_SIZE = 20;

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleTransformer.class);

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

    /**
     * @see Transformer#transform(DataSet, Configuration)
     */
    @Override
    public void transform(DataSet input, Configuration configuration) {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null.");
        }
        final TransformerWriter writer = writersService.getWriter(configuration.formatId(), configuration.output(),
                configuration.getArguments());
        try {
            writer.startObject();
            final ParsedActions parsedActions = actionParser.parse(configuration.getActions());
            final List<DataSetRowAction> rowActions = parsedActions.getRowTransformers();
            final boolean transformColumns = input.getMetadata() != null
                    && !input.getMetadata().getRowMetadata().getColumns().isEmpty();
            TransformationContext context = configuration.getTransformationContext();
            final AtomicBoolean wroteMetadata = new AtomicBoolean(false);
            // Row transformations
            Stream<DataSetRow> records = input.getRecords();
            // Apply actions to records
            for (DataSetRowAction action : rowActions) {
                records = records.map(r -> action.apply(r, new ActionContext(context)));
            }
            // Analyze content after all actions were applied to row
            records = records.map(r -> {
                if (transformColumns) {
                    // Use analyzer (for empty values, semantic...)
                    if (!r.isDeleted()) {
                        final DataSetRow row = r.order(r.getRowMetadata().getColumns());
                        configureAnalyzer(context, row).analyze(row.toArray(DataSetRow.SKIP_TDP_ID));
                    }
                }
                return r;
            });
            // Write transformed records to stream
            final Stack<DataSetRow> postProcessQueue = new Stack<>(); // Stack to remember last processed row.

            writer.fieldName("records");
            writer.startArray();
            records.forEach(row -> {
                try {
                    // get the current row
                    if (!postProcessQueue.empty()) {
                        postProcessQueue.pop();
                    }

                    // so far, other writers than JsonWriter do not need updated statistics on columns. Hence writing
                    // metadata at the beginning is no problem
                    if (writer.requireMetadataForHeader() && !wroteMetadata.get()) {
                        writer.write(row.getRowMetadata());
                        wroteMetadata.set(true);
                    }

                    // write the row if needed
                    if (row.shouldWrite()) {
                        writer.write(row);
                        // when a row is written, RowMetadata cannot be changed anymore, that's why action contexts are
                        // 'frozen'
                        context.freezeActionContexts();
                    }

                    // save the current row for latter (see below)
                    postProcessQueue.push(row); // In the end, last row remains in stack.
                } catch (IOException e) {
                    throw new TDPException(TransformationErrorCodes.UNABLE_TRANSFORM_DATASET, e);
                }
            });
            writer.endArray();

            // write metadata with updated columns statistics only when needed
            if (!wroteMetadata.get() && transformColumns) {
                writer.fieldName("metadata");
                writer.startObject();
                writer.fieldName("columns");

                final DataSetRow row = postProcessQueue.pop();

                // End analysis and set the statistics
                emptyInitialAnalysisBuffer(context, row); // Call in case row number < ANALYSIS_BUFFER_SIZE
                final Analyzer<Analyzers.Result> analyzer = (Analyzer<Analyzers.Result>) context.get(CONTEXT_ANALYZER);
                if (analyzer != null) {
                    // Analyzer may not be initialized when all rows were deleted.
                    analyzer.end();
                    adapter.adapt(row.getRowMetadata().getColumns(), analyzer.getResult());
                }

                // write the columns metadata
                writer.write(row.getRowMetadata());
                writer.endObject();
            }
            if (postProcessQueue.size() > 1) {
                LOGGER.warn("Too many processed rows in stack (expected 1, got {}).", postProcessQueue.size());
            }
            postProcessQueue.clear(); // Clear last processed row.
            writer.endObject();
            writer.flush();
        } catch (IOException e) {
            throw new TDPException(TransformationErrorCodes.UNABLE_TRANSFORM_DATASET, e);
        } finally {
            currentAnalysisStatus.remove();
            initialAnalysisBuffer.remove();
            // cleanup context (to make sure resources are properly closed)
            configuration.getTransformationContext().cleanup();
        }
    }

    @Override
    public boolean accept(Configuration configuration) {
        return Configuration.class.equals(configuration.getClass()) && configuration.volume() == Configuration.Volume.SMALL;
    }

    /**
     * An implementation of {@link Analyzer} that does not record nor analyze anything.
     */
    private static class NullAnalyzer implements Analyzer<Analyzers.Result> {

        private static final long serialVersionUID = 1L;

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
