package org.talend.dataprep.transformation.api.action.metadata;

import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;

import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.i18n.MessagesBundle;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;
import org.talend.dataprep.transformation.api.action.parameters.Item;
import org.talend.dataprep.transformation.api.action.parameters.Parameter;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Model an action to perform on a dataset.
 *
 * At row level, a closure is created for each row, see {@link ActionMetadata#create(Map)}. At row metadata level, a
 * closure is created for the row metadata, see {@link ActionMetadata#createMetadataClosure(Map)}.
 *
 * The actions are called from the
 */
public interface ActionMetadata {

    String ACTION_BEAN_PREFIX = "action#"; //$NON-NLS-1$

    /**
     * @return A unique name used to identify action.
     */
    String getName();

    /**
     * @return The label of the parameter, translated in the user locale.
     * @see MessagesBundle
     */
    default String getLabel() {
        return MessagesBundle.getString("action." + getName() + ".label");
    }

    /**
     * @return The description of the parameter, translated in the user locale.
     * @see MessagesBundle
     */
    default String getDescription() {
        return MessagesBundle.getString("action." + getName() + ".desc");
    }

    /**
     * @return A 'category' for the action used to group similar actions (eg. 'math', 'repair'...).
     */
    String getCategory();

    /**
     * Returns the list of multiple valued parameters required for this Action to be executed. represented as list box
     * on the front end.
     * 
     * @return A list of {@link org.talend.dataprep.transformation.api.action.parameters.Item items}. This should never
     * return null, actions with no item should return empty list.
     **/
    Item[] getItems();

    /**
     * @return Returns the list of input parameters required for this Action to be executed. represent as text input
     * field on the front end.
     **/
    Parameter[] getParameters();

    /**
     * Return true if the action can be applied to the given column metadata.
     * 
     * @param column the column metadata to transform.
     * @return true if the action can be applied to the given column metadata.
     */
    boolean accept(ColumnMetadata column);

    /**
     * Create a closure to perform the transformation on a DatasetRow according to the parameter.
     * 
     * @param parameters A key/value map holding all action dependent configuration.
     * @return A closure that accepts a DatasetRow, closures are expected to execute safely.
     */
    default BiConsumer<DataSetRow, TransformationContext> create(Map<String, String> parameters) {
        return (row, context) -> {
            // default empty implementation
        };
    }

    /**
     * Create a closure to perform the transformation at row metadata given the parameters.
     *
     * By default, the original row metadata is returned.
     *
     * @param parameters the parameters needed to perform the action.
     * @return A closure that accepts the dataset row metadata, closures are expected to execute safely.
     */
    default BiConsumer<RowMetadata, TransformationContext> createMetadataClosure(Map<String, String> parameters) {
        return (rowMetadata, context) -> {
            // default empty implementation
        };
    }

    /**
     * Parse the given json parameter into a map<key, value>.
     *
     * @param parameters the json parameters.
     * @return the action parameters as a map<key, value>.
     */
    default Map<String, String> parseParameters(Iterator<Map.Entry<String, JsonNode>> parameters) {
        return ActionMetadataUtils.parseParameters(parameters, this);
    }

    /**
     * @return True if the action is dynamic (i.e the parameters depends on the context
     * (dataset/preparation/previous_actions)
     */
    default boolean isDynamic() {
        return false;
    }

    /**
     * Adapts the current action metadata to the column. This method may return <code>this</code> if no action specific
     * change should be done. It may return a different instance with information from column (like a default value
     * inferred from column's name).
     * 
     * @param column A {@link ColumnMetadata column} information.
     * @return <code>this</code> if no change is required or a new action metadata with information extracted from
     * <code>column</code>.
     */
    default ActionMetadata adapt(ColumnMetadata column) {
        return this;
    }
}
