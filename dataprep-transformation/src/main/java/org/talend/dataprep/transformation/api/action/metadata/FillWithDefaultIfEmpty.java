package org.talend.dataprep.transformation.api.action.metadata;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.talend.dataprep.api.DataSetRow;
import org.talend.dataprep.api.type.Types;
import org.talend.dataprep.transformation.api.action.ActionParser;

public class FillWithDefaultIfEmpty implements ActionMetadata {

    public static final String         COLUMN_NAME_PARAMETER   = "column_name";                                     //$NON-NLS-1$

    public static final String         DEFAULT_VALUE_PARAMETER = "default_value";                                   //$NON-NLS-1$

    public static final String         FILL_EMPTY_ACTION_NAME  = "fillemptywithdefault";                            //$NON-NLS-1$

    public static final String         FILL_EMPTY_ACTION_DESC  = "fill empty cells of this column with given value"; //$NON-NLS-1$

    public static final ActionMetadata INSTANCE                = new FillWithDefaultIfEmpty();

    protected FillWithDefaultIfEmpty() {
    }

    @Override
    public String getName() {
        return FILL_EMPTY_ACTION_NAME;
    }

    @Override
    public String getDescription() {
        return FILL_EMPTY_ACTION_DESC;
    }

    @Override
    public Type getType() {
        return Type.OPERATION;
    }

    @Override
    public String getCategory() {
        return "repair";
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
        return new Parameter[] { new Parameter(COLUMN_NAME_PARAMETER, Types.STRING.getName(), StringUtils.EMPTY),
                new Parameter(DEFAULT_VALUE_PARAMETER, Types.STRING.getName(), StringUtils.EMPTY) };
    }

    @Override
    public Consumer<DataSetRow> create(Iterator<Map.Entry<String, JsonNode>> parameters) {
        Map<String, String> parsedParameters = new HashMap<>();
        while (parameters.hasNext()) {
            Map.Entry<String, JsonNode> currentParameter = parameters.next();
            switch (currentParameter.getKey()) {
            case COLUMN_NAME_PARAMETER:
                parsedParameters.put(COLUMN_NAME_PARAMETER, currentParameter.getValue().getTextValue());
                break;
            case DEFAULT_VALUE_PARAMETER:
                parsedParameters.put(DEFAULT_VALUE_PARAMETER, currentParameter.getValue().asText());
                break;
            default:
                ActionParser.LOGGER
                        .warn("Parameter '" + currentParameter.getKey() + "' is not recognized for " + this.getClass());
            }
        }
        return row -> {
            String columnName = parsedParameters.get(COLUMN_NAME_PARAMETER);
            String value = row.get(columnName);
            if (value == null || value.trim().length() == 0) {
                row.set(columnName, parsedParameters.get(DEFAULT_VALUE_PARAMETER));
            }
        };
    }
}
