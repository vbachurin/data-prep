package org.talend.dataprep.transformation.api.action.metadata;

import java.util.*;
import java.util.function.Consumer;

import org.codehaus.jackson.JsonNode;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.i18n.MessagesBundle;
import org.talend.dataprep.transformation.api.action.ActionParser;

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
     * @return A list of {@link Item items}. This should never return null, actions with no item should return empty
     * list.
     **/
    Item[] getItems();

    /**
     * @return Returns the list of input parameters required for this Action to be executed. represent as text input
     * field on the front end.
     **/
    Parameter[] getParameters();

    /**
     * Return the list of column type that this action can applied to.
     *
     * @return A set of the column {@link Type types} this Action can handle.
     */
    Set<Type> getCompatibleColumnTypes();

    /**
     * @param input Action parameters as json input.
     * @return the closure that transforms the row.
     */
    default Consumer<DataSetRow> create(Iterator<Map.Entry<String, JsonNode>> input) {
        Map<String, String> parsedParameters = parseParameters(input);
        return create(parsedParameters);
    }

    /**
     * Create a closure to perform the transformation on a DatasetRow according to the parameter.
     * 
     * @param parameters A key/value map holding all action dependent configuration.
     * @return A closure that accepts a DatasetRow, closures are expected to execute safely.
     */
    default Consumer<DataSetRow> create(Map<String, String> parameters) {
        return row -> {
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
    default Consumer<RowMetadata> createMetadataClosure(Map<String, String> parameters) {
        return rowMetadata -> {
        };
    }

    /**
     * Parse the given json parameter into a map<key, value>.
     *
     * @param parameters the json parameters.
     * @return the action parameters as a map<key, value>.
     */
    default Map<String, String> parseParameters(Iterator<Map.Entry<String, JsonNode>> parameters) {
        List<String> paramIds = new ArrayList<>();
        for (Parameter current : getParameters()) {
            paramIds.add(current.getName());
        }
        for (Item current : getItems()) {
            paramIds.add(current.getName());
            for (Item.Value value : current.getValues()) {
                for (Parameter parameter : value.getParameters()) {
                    paramIds.add(parameter.getName());
                }
            }
        }

        Map<String, String> parsedParameters = new HashMap<>();
        while (parameters.hasNext()) {
            Map.Entry<String, JsonNode> currentParameter = parameters.next();

            if (paramIds.contains(currentParameter.getKey())) {
                parsedParameters.put(currentParameter.getKey(), currentParameter.getValue().asText());
            } else {
                ActionParser.LOGGER.warn("Parameter '{} is not recognized for {}", //
                        currentParameter.getKey(), //
                        this.getClass());
            }
        }
        return parsedParameters;
    }

}
