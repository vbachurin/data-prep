package org.talend.dataprep.transformation.api.action.metadata;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataprep.transformation.api.action.parameters.Item;
import org.talend.dataprep.transformation.api.action.parameters.Parameter;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Utility class for the ActionsMetadata
 */
class ActionMetadataUtils {

    /** This class' logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ActionMetadataUtils.class);

    /**
     * Default empty constructor.
     */
    private ActionMetadataUtils() {
        // private constructor for utility class
    }

    /**
     * Parse the given json parameter into a map<key, value>.
     *
     * @param parameters the json parameters.
     * @param actionMetadata the action metadata.
     * @return the action parameters as a map<key, value>.
     */
    public static Map<String, String> parseParameters(Iterator<Map.Entry<String, JsonNode>> parameters,
            ActionMetadata actionMetadata) {
        List<String> paramIds = new ArrayList<>();
        for (Parameter current : actionMetadata.getParameters()) {
            paramIds.add(current.getName());
        }
        for (Item current : actionMetadata.getItems()) {
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
                LOGGER.warn("Parameter '{} is not recognized for {}", //
                        currentParameter.getKey(), //
                        actionMetadata.getClass());
            }
        }
        return parsedParameters;
    }
}
