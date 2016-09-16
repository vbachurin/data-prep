package org.talend.dataprep.transformation.pipeline;

import static java.util.stream.Collectors.toSet;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSet;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.api.dataset.row.DataSetRow;
import org.talend.dataprep.api.dataset.statistics.StatisticsAdapter;
import org.talend.dataprep.api.preparation.Action;
import org.talend.dataprep.parameters.Parameter;
import org.talend.dataprep.parameters.ParameterType;
import org.talend.dataprep.quality.AnalyzerService;
import org.talend.dataprep.transformation.actions.common.ActionMetadata;
import org.talend.dataprep.transformation.actions.common.ImplicitParameters;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;
import org.talend.dataprep.transformation.api.transformer.json.NullAnalyzer;
import org.talend.dataprep.transformation.pipeline.node.*;
import org.talend.dataquality.common.inference.Analyzer;
import org.talend.dataquality.common.inference.Analyzers;

public class Pipeline implements Node, RuntimeNode {

    private static final Logger LOGGER = LoggerFactory.getLogger(Pipeline.class);

    private final Node node;

    /**
     * @param node The source node (the node in the pipeline that submit content to the pipeline).
     * @see Builder to create a new instance of this class.
     */
    Pipeline(Node node) {
        this.node = node;
    }

    public void execute(DataSet dataSet) {
        final RowMetadata rowMetadata = dataSet.getMetadata().getRowMetadata().clone();
        try (Stream<DataSetRow> records = dataSet.getRecords()) {
            records.forEach(r -> node.exec().receive(r, rowMetadata));
            node.exec().signal(Signal.END_OF_STREAM);
        }
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        accept(new PipelineConsoleDump(builder));
        return builder.toString();
    }

    @Override
    public void receive(DataSetRow row, RowMetadata metadata) {
        node.exec().receive(row, metadata);
    }

    @Override
    public Link getLink() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLink(Link link) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void signal(Signal signal) {
        node.exec().signal(signal);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitPipeline(this);
    }

    @Override
    public RuntimeNode exec() {
        return this;
    }

    public Node getNode() {
        return node;
    }

    public static class Builder {

        private final List<Action> actions = new ArrayList<>();

        private final Map<Action, ActionMetadata> actionToMetadata = new HashMap<>();

        private Function<List<ColumnMetadata>, Analyzer<Analyzers.Result>> inlineAnalyzer = l -> Analyzers
                .with(NullAnalyzer.INSTANCE);

        private Function<List<ColumnMetadata>, Analyzer<Analyzers.Result>> delayedAnalyzer = l -> Analyzers
                .with(NullAnalyzer.INSTANCE);

        private RowMetadata rowMetadata;

        private boolean completeMetadata;

        private ActionRegistry actionRegistry;

        private TransformationContext context;

        private StatisticsAdapter adapter;

        private Supplier<Node> monitorSupplier = BasicNode::new;

        private Supplier<Node> outputSupplier = () -> NullNode.INSTANCE;

        private boolean allowMetadataChange = true;

        private Predicate<DataSetRow> inFilter;

        private Function<RowMetadata, Predicate<DataSetRow>> outFilter;

        private boolean needGlobalStatistics = true;

        private AnalyzerService analyzerService;

        private int actionIndex = 0;

        public static Builder builder() {
            return new Builder();
        }

        public Builder withAnalyzerService(AnalyzerService analyzerService) {
            this.analyzerService = analyzerService;
            return this;
        }

        public Builder withStatisticsAdapter(StatisticsAdapter adapter) {
            this.adapter = adapter;
            return this;
        }

        public Builder withContext(TransformationContext context) {
            this.context = context;
            return this;
        }

        public Builder withActionRegistry(ActionRegistry actionRegistry) {
            this.actionRegistry = actionRegistry;
            return this;
        }

        public Builder withInitialMetadata(RowMetadata rowMetadata, boolean completeMetadata) {
            this.rowMetadata = rowMetadata;
            this.completeMetadata = completeMetadata;
            return this;
        }

        public Builder withActions(List<Action> actions) {
            this.actions.addAll(actions);
            return this;
        }

        public Builder withInlineAnalysis(Function<List<ColumnMetadata>, Analyzer<Analyzers.Result>> analyzer) {
            this.inlineAnalyzer = analyzer;
            return this;
        }

        public Builder withDelayedAnalysis(Function<List<ColumnMetadata>, Analyzer<Analyzers.Result>> analyzer) {
            this.delayedAnalyzer = analyzer;
            return this;
        }

        public Builder withMonitor(Supplier<Node> monitorSupplier) {
            this.monitorSupplier = monitorSupplier;
            return this;
        }

        public Builder withOutput(Supplier<Node> outputSupplier) {
            this.outputSupplier = outputSupplier;
            return this;
        }

        public Builder withGlobalStatistics(boolean needGlobalStatistics) {
            this.needGlobalStatistics = needGlobalStatistics;
            return this;
        }

        public Builder allowMetadataChange(boolean allowMetadataChange) {
            this.allowMetadataChange = allowMetadataChange;
            return this;
        }

        public Builder withFilter(Predicate<DataSetRow> filter) {
            this.inFilter = filter;
            return this;
        }

        private ActionAnalysis analyzeActions() {
            // Compile actions
            final Set<String> originalColumns = rowMetadata.getColumns()
                    .stream()
                    .map(ColumnMetadata::getId)
                    .collect(toSet());
            final Set<String> valueModifiedColumns = new HashSet<>();
            final Set<String> metadataModifiedColumns = new HashSet<>();
            int createColumnActions = 0;

            boolean needFullAnalysis = false;
            boolean needOnlyInvalidAnalysis = false;
            Predicate<ColumnMetadata> filterForFullAnalysis = c -> true;
            Predicate<ColumnMetadata> filterForInvalidAnalysis = c -> true;

            if (actionRegistry != null) {
                for (Action action : actions) {
                    final ActionMetadata actionMetadata = actionRegistry.get(action.getName());
                    actionToMetadata.put(action, actionMetadata);
                }
                // Analyze what columns to look at during analysis
                for (Map.Entry<Action, ActionMetadata> entry : actionToMetadata.entrySet()) {
                    final ActionMetadata actionMetadata = entry.getValue();
                    final Action action = entry.getKey();
                    Set<ActionMetadata.Behavior> behavior = actionMetadata.getBehavior();
                    for (ActionMetadata.Behavior currentBehavior : behavior) {
                        switch (currentBehavior) {
                            case VALUES_ALL:
                                // All values are going to be changed, and all original columns are going to be modified.
                                valueModifiedColumns.addAll(originalColumns);
                                break;
                            case METADATA_CHANGE_TYPE:
                                metadataModifiedColumns.add(action.getParameters().get(ImplicitParameters.COLUMN_ID.getKey()));
                                break;
                            case VALUES_COLUMN:
                                valueModifiedColumns.add(action.getParameters().get(ImplicitParameters.COLUMN_ID.getKey()));
                                break;
                            case VALUES_MULTIPLE_COLUMNS:
                                // Add the action's source column
                                valueModifiedColumns.add(action.getParameters().get(ImplicitParameters.COLUMN_ID.getKey()));
                                // ... then add all column parameter (COLUMN_ID is string, not column)
                                final List<Parameter> parameters = actionMetadata.getParameters();
                                valueModifiedColumns.addAll(parameters.stream() //
                                        .filter(parameter -> ParameterType
                                                .valueOf(parameter.getType().toUpperCase()) == ParameterType.COLUMN) //
                                        .map(parameter -> action.getParameters().get(parameter.getName())) //
                                        .collect(Collectors.toList()));
                                break;
                            case METADATA_COPY_COLUMNS:
                                // TODO Ignore column copy from analysis (metadata did not change)
                                break;
                            case METADATA_CREATE_COLUMNS:
                                createColumnActions++;
                                break;
                            case METADATA_DELETE_COLUMNS:
                            case METADATA_CHANGE_NAME:
                                // Do nothing: no need to re-analyze where only name was changed.
                                break;
                            default:
                                break;
                        }
                    }
                }

                // when values are modified, we need to do a full analysis (schema + invalid + stats)
                needFullAnalysis = !valueModifiedColumns.isEmpty() || createColumnActions > 0;
                // when only metadata is modified, we need to re-evaluate the invalids entries
                needOnlyInvalidAnalysis = !needFullAnalysis && !metadataModifiedColumns.isEmpty();
                // only the columns with modified values or new columns need the schema + stats analysis
                filterForFullAnalysis = c -> valueModifiedColumns.contains(c.getId()) || !originalColumns.contains(c.getId());
                // only the columns with metadata change or value changes need to re-evaluate invalids
                filterForInvalidAnalysis = filterForFullAnalysis.or(c -> metadataModifiedColumns.contains(c.getId()));

            } else {
                LOGGER.warn("Unable to statically analyze actions (no action registry defined).");
            }
            return new ActionAnalysis(needFullAnalysis, needOnlyInvalidAnalysis, filterForFullAnalysis, filterForInvalidAnalysis);
        }

        public Builder withFilterOut(Function<RowMetadata, Predicate<DataSetRow>> outFilter) {
            this.outFilter = outFilter;
            return this;
        }

        private void addReservoirStatistics(Action action, ActionAnalysis analysis, NodeBuilder builder) {
            if (actionIndex == 0 && completeMetadata) {
                LOGGER.debug("No need for statistics, action '{}' is the first action in pipeline.", action);
                return;
            } else if (actionIndex == 0) { // hasMonitoring = true
                LOGGER.debug(
                        "Recomputing statistics, action '{}' is the first action in pipeline but running on all data (including unknown values).",
                        action);
            }
            if (allowMetadataChange) {
                final Set<ActionMetadata.Behavior> behavior = actionToMetadata.get(action).getBehavior();
                if (behavior.contains(ActionMetadata.Behavior.NEED_STATISTICS_PATTERN)) {
                    builder.to(new TypeDetectionNode(c -> analyzerService.build(c, AnalyzerService.Analysis.PATTERNS), analysis.filterForFullAnalysis, adapter));
                }
                if (behavior.contains(ActionMetadata.Behavior.NEED_STATISTICS_INVALID)) {
                    builder.to(new TypeDetectionNode(inlineAnalyzer, analysis.filterForFullAnalysis, adapter));
                    builder.to(new InvalidDetectionNode(analyzerService, analysis.filterForInvalidAnalysis));
                }
                if (action.getParameters().containsKey(ImplicitParameters.FILTER.getKey())) {
                    // action has a filterForFullAnalysis, to cover cases where filters are on invalid values
                    final String filterAsString = action.getParameters().get(ImplicitParameters.FILTER.getKey());
                    if (StringUtils.contains(filterAsString, "valid") || StringUtils.contains(filterAsString, "invalid")) {
                        // TODO Perform static analysis of filterForFullAnalysis to discover which column is the filterForFullAnalysis on.
                        builder.to(new TypeDetectionNode(inlineAnalyzer, analysis.filterForFullAnalysis, adapter));
                        builder.to(new InvalidDetectionNode(analyzerService, analysis.filterForInvalidAnalysis));
                    }
                }
            }
        }

        public Pipeline build() {
            final ActionAnalysis analysis = analyzeActions();
            final NodeBuilder current;
            if (inFilter != null) {
                current = NodeBuilder.filteredSource(inFilter);
            } else {
                current = NodeBuilder.source();
            }
            if (rowMetadata.getColumns().isEmpty()) {
                LOGGER.debug("No initial metadata submitted for transformation, computing new one.");
                current.to(new TypeDetectionNode(inlineAnalyzer, c -> true, adapter));
            }
            // Apply actions
            for (Action action : actions) {
                addReservoirStatistics(action, analysis, current);
                current.to(new CompileNode(action, context.create(action.getRowAction())));
                current.to(new ActionNode(action, context.in(action.getRowAction())));
                actionIndex++; // Keep track of the being applied action (to decide whether recomputing stats is worth it).
            }
            // Analyze (delayed)
            if (needGlobalStatistics) {
                final Node[] statisticsNodes = analysis.getStatisticsNodes();
                for (final Node nextStatsNode: statisticsNodes) {
                    current.to(nextStatsNode);
                }
            }
            // Output
            if (outFilter != null) {
                current.to(new FilteredNode(outFilter));
            }
            current.to(new CleanUpNode(context));
            current.to(outputSupplier.get());
            current.to(monitorSupplier.get());
            // Finally build pipeline
            return new Pipeline(current.build());
        }

        private class ActionAnalysis {

            private final boolean needFullAnalysis;
            private final boolean needOnlyInvalidAnalysis;
            private final Predicate<ColumnMetadata> filterForFullAnalysis;
            private final Predicate<ColumnMetadata> filterForInvalidAnalysis;

            public ActionAnalysis(final boolean needFullAnalysis, final boolean needOnlyInvalidAnalysis, final Predicate<ColumnMetadata> filterForFullAnalysis, final Predicate<ColumnMetadata> filterForInvalidAnalysis) {
                this.needFullAnalysis = needFullAnalysis;
                this.needOnlyInvalidAnalysis = needOnlyInvalidAnalysis;
                this.filterForFullAnalysis = filterForFullAnalysis;
                this.filterForInvalidAnalysis = filterForInvalidAnalysis;
            }

            public Node[] getStatisticsNodes() {
                if (needFullAnalysis) {
                    return new Node[]{
                            new TypeDetectionNode(inlineAnalyzer, filterForFullAnalysis, adapter),
                            new InvalidDetectionNode(analyzerService, filterForInvalidAnalysis),
                            new StatisticsNode(delayedAnalyzer, filterForFullAnalysis, adapter)
                    };
                }

                if (needOnlyInvalidAnalysis) {
                    return new Node[]{
                            new InvalidDetectionNode(analyzerService, filterForInvalidAnalysis),
                            //TODO JSO do only quality analysis
                            new StatisticsNode(delayedAnalyzer, filterForInvalidAnalysis, adapter)
                    };
                }
                return new Node[0];
            }
        }

    }

}
