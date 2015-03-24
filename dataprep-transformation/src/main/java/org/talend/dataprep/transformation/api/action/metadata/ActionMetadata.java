package org.talend.dataprep.transformation.api.action.metadata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.codehaus.jackson.JsonNode;
import org.talend.dataprep.api.DataSetRow;
import org.talend.dataprep.i18n.MessagesBundle;
import org.talend.dataprep.transformation.api.action.ActionParser;

public interface ActionMetadata {

    public static final String ACTION_BEAN_PREFIX = "action."; //$NON-NLS-1$

    String getName();

    /**
     * the label of the parameter, translated in the user locale.
     */
    default String getLabel() {
        return MessagesBundle.getString("action." + getName() + ".label");
    }

    /**
     * the description of the parameter, translated in the user locale.
     */
    default String getDescription() {
        return MessagesBundle.getString("action." + getName() + ".desc");
    }

    String getCategory();

    /**
     * return the list of multiple valued parameters required for this Action to be executed. represented as list box on
     * the front end.
     **/
    Item[] getItems();

    /**
     * return the list of input parameters required for this Action to be executed. represent as text input field on the
     * front end.
     **/
    Parameter[] getParameters();

    Consumer<DataSetRow> create(Map<String, String> parsedParameters);

    default Consumer<DataSetRow> create(Iterator<Map.Entry<String, JsonNode>> input) {
        Map<String, String> parsedParameters = parseParameters(input);
        return create(parsedParameters);
    }

    default Map<String, String> parseParameters(Iterator<Map.Entry<String, JsonNode>> parameters) {
        List<String> paramIds = new ArrayList<>();
        for (Parameter current : getParameters()) {
            paramIds.add(current.getName());
        }
        for (Item current : getItems()) {
            paramIds.add(current.getName());
        }

        Map<String, String> parsedParameters = new HashMap<>();
        while (parameters.hasNext()) {
            Map.Entry<String, JsonNode> currentParameter = parameters.next();

            if (paramIds.contains(currentParameter.getKey())) {
                parsedParameters.put(currentParameter.getKey(), currentParameter.getValue().asText());
            } else {
                System.out.println("### " + "Parameter '" + currentParameter.getKey() + "' is not recognized for "
                        + this.getClass());
                ActionParser.LOGGER
                        .warn("Parameter '" + currentParameter.getKey() + "' is not recognized for " + this.getClass());
            }
        }
        return parsedParameters;
    }

}
