package org.talend.dataprep.transformation.api.action.metadata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.talend.dataprep.api.DataSetRow;
import org.talend.dataprep.api.type.Types;
import org.talend.dataprep.transformation.api.action.ActionParser;

public abstract class AbstractDelete implements ActionMetadata {

    public static final String         COLUMN_NAME_PARAMETER    = "column_name";                          //$NON-NLS-1$

    public Type getType() {
        return Type.OPERATION;
    }

    @Override
    public String getCategory() {
        return "case";
    }

    @Override
    public Item[] getItems() {
        return new Item[0];
    }

    @Override
    public String getValue() {
        return StringUtils.EMPTY;
    }

    @Override
    public Parameter[] getParameters() {
        return new Parameter[] { new Parameter(COLUMN_NAME_PARAMETER, Types.STRING.getName(), StringUtils.EMPTY) };
    }

    public abstract boolean toDelete(Map<String, String> parsedParameters, String value);

    @Override
    public Consumer<DataSetRow> create(Iterator<Map.Entry<String, JsonNode>> parameters) {
        Map<String, String> parsedParameters = parseParameters(parameters);
        return row -> {
            String columnName = parsedParameters.get(COLUMN_NAME_PARAMETER);
            String value = row.get(columnName);
            if (toDelete(parsedParameters, value)) {
                row.setDeleted(true);
            }
        };
    }

    private Map<String, String> parseParameters(Iterator<Map.Entry<String, JsonNode>> parameters) {
        List<String> paramIds = new ArrayList<>();
        for (Parameter current : getParameters()) {
            paramIds.add(current.getName());
        }

        Map<String, String> parsedParameters = new HashMap<>();
        while (parameters.hasNext()) {
            Map.Entry<String, JsonNode> currentParameter = parameters.next();

            if (paramIds.contains(currentParameter.getKey())) {
                parsedParameters.put(currentParameter.getKey(), currentParameter.getValue().getTextValue());
            } else {
                ActionParser.LOGGER
                        .warn("Parameter '" + currentParameter.getKey() + "' is not recognized for " + this.getClass());
            }
        }
        return parsedParameters;
    }
}
