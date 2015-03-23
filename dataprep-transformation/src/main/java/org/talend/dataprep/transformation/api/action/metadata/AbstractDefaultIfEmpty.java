package org.talend.dataprep.transformation.api.action.metadata;

import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.lang.StringUtils;
import org.talend.dataprep.api.DataSetRow;

public abstract class AbstractDefaultIfEmpty implements ActionMetadata {

    public static final String COLUMN_NAME_PARAMETER   = "column_name";  //$NON-NLS-1$

    public static final String DEFAULT_VALUE_PARAMETER = "default_value"; //$NON-NLS-1$

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
    public Consumer<DataSetRow> create(Map<String, String> parsedParameters) {
        return row -> {
            String columnName = parsedParameters.get(COLUMN_NAME_PARAMETER);
            String value = row.get(columnName);
            if (value == null || value.trim().length() == 0) {
                row.set(columnName, parsedParameters.get(DEFAULT_VALUE_PARAMETER));
            }
        };
    }
}
