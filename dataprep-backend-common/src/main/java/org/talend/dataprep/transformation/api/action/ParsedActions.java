package org.talend.dataprep.transformation.api.action;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;

import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.preparation.Action;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;

/**
 * JavaBean that holds all the transformers.
 */
public class ParsedActions {

    /** The row transformers united into a single consumer. */
    private final List<DataSetRowAction> rowTransformers = new LinkedList<>();


    /** The list of all actions that led to closure creation */
    private List<Action> allActions;

    /**
     * Default constructor.
     *
     * @param rowTransformer The unique row transformer united into a single consumer.
     */
    public ParsedActions(DataSetRowAction rowTransformer) {
        this.rowTransformers.add(rowTransformer);
    }

    /**
     * Default constructor.
     *
     * @param allActions The list of all actions that led to closure creation
     * @param rowTransformers The row transformers united into a single consumer.
     */
    public ParsedActions(List<Action> allActions, List<DataSetRowAction> rowTransformers) {
        this.allActions = allActions;
        this.rowTransformers.addAll(rowTransformers);
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
     * @return all the actions.
     */
    public List<Action> getAllActions() {
        return allActions;
    }
}
