package org.talend.dataprep.transformation.api.action.metadata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

import org.codehaus.jackson.JsonNode;
import org.talend.dataprep.api.DataSetRow;
import org.talend.dataprep.transformation.api.action.ActionParser;
import org.talend.dataprep.transformation.i18n.MessagesBundle;

public interface ActionMetadata {

    String getName();

    /**
     * Temp method, cleaner solution should use Spring mappoiing with following method.
     */
    default String getLabel() {
        return getLabel(Locale.ENGLISH);
    }

    /**
     * Temp method, cleaner solution should use Spring mappoiing with following method.
     */
    default String getDescription() {
        return getDescription(Locale.ENGLISH);
    }

    /**
     * the label of the parameter, translated in the user locale.
     */
    default String getLabel(Locale locale) {
        return MessagesBundle.getString(locale, "action." + getName() + ".label");
    }

    /**
     * the description of the parameter, translated in the user locale.
     */
    default String getDescription(Locale locale) {
        return MessagesBundle.getString(locale, "action." + getName() + ".desc");
    }

    Type getType();

    String getCategory();

    Item[] getItems();

    String getValue();

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
