package org.talend.dataprep.transformation.api.action.metadata;

import static org.talend.dataprep.api.preparation.Action.Builder.builder;

import java.util.Map;

import org.talend.dataprep.api.preparation.Action;

public abstract class AbstractDefaultIfEmpty extends SingleColumnAction {

    public static final String DEFAULT_VALUE_PARAMETER = "default_value"; //$NON-NLS-1$

    @Override
    public String getCategory() {
        return ActionCategory.QUICKFIX.getDisplayName();
    }

    @Override
    public Action create(Map<String, String> parameters) {
        return builder().withRow((row, context) -> {
            String columnName = parameters.get(COLUMN_ID);
            String value = row.get(columnName);
            if (value == null || value.trim().length() == 0) {
                row.set(columnName, parameters.get(DEFAULT_VALUE_PARAMETER));
            }
            return row;
        }).build();
    }
}
