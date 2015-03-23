package org.talend.dataprep.transformation.api.action.metadata;

import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.lang.StringUtils;
import org.talend.dataprep.api.DataSetRow;
import org.talend.dataprep.api.type.Types;

public abstract class AbstractDelete implements ActionMetadata {

    public static final String COLUMN_NAME_PARAMETER = "column_name"; //$NON-NLS-1$

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
    public Consumer<DataSetRow> create(Map<String, String> parsedParameters) {
        return row -> {
            String columnName = parsedParameters.get(COLUMN_NAME_PARAMETER);
            String value = row.get(columnName);
            if (toDelete(parsedParameters, value)) {
                row.setDeleted(true);
            }
        };
    }

}
