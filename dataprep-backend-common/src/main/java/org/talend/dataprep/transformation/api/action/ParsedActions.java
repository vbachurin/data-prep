package org.talend.dataprep.transformation.api.action;

import static org.talend.dataprep.transformation.api.action.AggregateActions.aggregate;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.api.preparation.Action;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;

/**
 * JavaBean that holds all the transformers.
 */
public class ParsedActions {

    /** The row transformers united into a single consumer. */
    private final List<DataSetRowAction> rowTransformers = new LinkedList<>();

    /** The list of metadata transformers. */
    private final List<DataSetMetadataAction> metadataTransformers = new LinkedList<>();

    /** The list of all actions that led to closure creation */
    private List<Action> allActions;

    /**
     * Default constructor.
     *
     * @param rowTransformer The unique row transformer united into a single consumer.
     * @param metadataTransformer The unique metadata transformer.
     */
    public ParsedActions(DataSetRowAction rowTransformer, DataSetMetadataAction metadataTransformer) {
        this.rowTransformers.add(rowTransformer);
        this.metadataTransformers.add(metadataTransformer);
    }

    /**
     * Default constructor.
     *
     * @param allActions The list of all actions that led to closure creation
     * @param rowTransformers The row transformers united into a single consumer.
     * @param metadataTransformers The list of metadata transformers.
     */
    public ParsedActions(List<Action> allActions, List<DataSetRowAction> rowTransformers, List<DataSetMetadataAction> metadataTransformers) {
        if (rowTransformers.size() != metadataTransformers.size()) {
            throw new IllegalArgumentException("Expected same number of metadata and row actions.");
        }
        this.allActions = allActions;
        this.rowTransformers.addAll(rowTransformers);
        this.metadataTransformers.addAll(metadataTransformers);
    }

    public List<DataSetRowAction> getRowTransformers() {
        return rowTransformers;
    }

    /**
     * @return The row transformers united into a single consumer.
     */
    public BiFunction<DataSetRow, TransformationContext, DataSetRow> asUniqueRowTransformer() {
        return AggregateFunctions.aggregate(rowTransformers);
    }

    /**
     * @return The list of metadata transformers.
     */
    @Deprecated
    public BiConsumer<RowMetadata, TransformationContext> asUniqueMetadataTransformer() {
        return aggregate(metadataTransformers);
    }

    public List<Action> getAllActions() {
        return allActions;
    }
}
