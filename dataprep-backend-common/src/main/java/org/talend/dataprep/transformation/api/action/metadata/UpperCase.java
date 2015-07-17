package org.talend.dataprep.transformation.api.action.metadata;

import static org.talend.dataprep.api.preparation.Action.Builder.builder;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.preparation.Action;
import org.talend.dataprep.api.type.Type;

/**
 * Uppercase a column in a row.
 */
@Component(UpperCase.ACTION_BEAN_PREFIX + UpperCase.UPPER_CASE_ACTION_NAME)
public class UpperCase extends SingleColumnAction {

    /** The action code name. */
    public static final String UPPER_CASE_ACTION_NAME = "uppercase"; //$NON-NLS-1$

    /**
     * @see ActionMetadata#getName()
     */
    @Override
    public String getName() {
        return UPPER_CASE_ACTION_NAME;
    }

    /**
     * @see ActionMetadata#getCategory()
     */
    @Override
    public String getCategory() {
        return ActionCategory.CASE.getDisplayName();
    }

    /**
     * @see ActionMetadata#create(Map)
     */
    @Override
    public Action create(Map<String, String> parameters) {
        return builder().withRow((row, context) -> {
            String columnId = parameters.get(COLUMN_ID);
            String value = row.get(columnId);
            if (value == null) {
                return row;
            }

            String newValue = value.toUpperCase();
            row.set(columnId, newValue);
            return row;
        }).build();
    }

    /**
     * @see ActionMetadata#accept(ColumnMetadata)
     */
    @Override
    public boolean accept(ColumnMetadata column) {
        return Type.STRING.equals(Type.get(column.getType()));
    }

}
