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
import org.talend.dataprep.transformation.api.action.metadata.Item.Value;

/**
 * Split a cell value on a separator.
 * 
 * THIS ACTION IS NOT MAPPED YET IN ActionParser NAND SUGGESTED. It was designed to test conditionnal parameters in ui.
 * Remove this comments when action is registered.
 */
public class Split implements ActionMetadata {

    public static final String         COLUMN_NAME_PARAMETER      = "column_name";     //$NON-NLS-1$

    // The separator shown to the user as a list. An item in this list is the value 'other', which allow the user to
    // manually enter its separator.
    public static final String         SEPARATOR_PARAMETER        = "separator";       //$NON-NLS-1$

    // The separator manually specified by the user. Should be used only if SEPARATOR_PARAMETER value is 'other'
    public static final String         MANUAL_SEPARATOR_PARAMETER = "manual_separator"; //$NON-NLS-1$

    public static final String         SPLIT_ACTION_NAME          = "split";           //$NON-NLS-1$

    public static final ActionMetadata INSTANCE                   = new Split();

    private Split() {
    }

    @Override
    public String getName() {
        return SPLIT_ACTION_NAME;
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
    public String getValue() {
        return StringUtils.EMPTY;
    }

    @Override
    public Parameter[] getParameters() {
        return new Parameter[] { new Parameter(COLUMN_NAME_PARAMETER, Types.STRING.getName(), StringUtils.EMPTY) };
    }

    public Item[] getItems() {
        Value[] values = new Value[] { new Value(":", true), new Value("@"),
                new Value("other", new Parameter(MANUAL_SEPARATOR_PARAMETER, Types.STRING.getName(), StringUtils.EMPTY)) };
        return new Item[] { new Item(SEPARATOR_PARAMETER, Type.LIST, "categ", values) };
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
            case SEPARATOR_PARAMETER:
                parsedParameters.put(SEPARATOR_PARAMETER, currentParameter.getValue().getTextValue());
                break;
            case MANUAL_SEPARATOR_PARAMETER:
                parsedParameters.put(MANUAL_SEPARATOR_PARAMETER, currentParameter.getValue().getTextValue());
                break;
            default:
                ActionParser.LOGGER
                        .warn("Parameter '" + currentParameter.getKey() + "' is not recognized for " + this.getClass());
            }
        }

        String realSeparator = (parsedParameters.get(SEPARATOR_PARAMETER).equals("other") ? parsedParameters
                .get(MANUAL_SEPARATOR_PARAMETER) : parsedParameters.get(SEPARATOR_PARAMETER));

        return row -> {
            String columnName = parsedParameters.get(COLUMN_NAME_PARAMETER);
            String value = row.get(columnName);
            if (value != null) {
                String[] split = value.split(realSeparator);
                for (int i = 0; i < split.length; i++) {
                    row.set(columnName + "_split_" + i, split[i]);
                }
            }
        };
    }
}
