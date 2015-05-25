package org.talend.dataprep.transformation.api.action.metadata;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.jackson.JsonNode;
import org.talend.dataprep.transformation.api.action.parameters.Item;

public abstract class AbstractDynamicAction extends SingleColumnAction {
    @Override
    public Item[] getItems() {
        return new Item[0];
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    /**
     * Get all the parameters without skiping any. The parameters are dynamics.
     * @param parameters the json parameters.
     */
    @Override
    public Map<String, String> parseParameters(Iterator<Entry<String, JsonNode>> parameters) {
        Map<String, String> parsedParameters = new HashMap<>();
        while (parameters.hasNext()) {
            Entry<String, JsonNode> currentParameter = parameters.next();
            parsedParameters.put(currentParameter.getKey(), currentParameter.getValue().asText());
        }
        return parsedParameters;
    }
}

