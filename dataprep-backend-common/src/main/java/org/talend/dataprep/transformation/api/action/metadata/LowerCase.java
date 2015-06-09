package org.talend.dataprep.transformation.api.action.metadata;

import java.util.Map;
import java.util.function.Consumer;

import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.type.Type;

/**
 * Lower case a column in a dataset row.
 */
@Component(LowerCase.ACTION_BEAN_PREFIX + LowerCase.LOWER_CASE_ACTION_NAME)
public class LowerCase extends SingleColumnAction {

    /** Action name. */
    public static final String LOWER_CASE_ACTION_NAME = "lowercase"; //$NON-NLS-1$

    /**
     * @see ActionMetadata#getName()
     */
    @Override
    public String getName() {
        return LOWER_CASE_ACTION_NAME;
    }

    /**
     * @see ActionMetadata#getCategory()
     */
    @Override
    public String getCategory() {
        return "case"; //$NON-NLS-1$
    }

    /**
     * @see ActionMetadata#create(Map)
     */
    @Override
    public Consumer<DataSetRow> create(Map<String, String> parameters) {
        return row -> {
            String columnName = parameters.get(COLUMN_ID);
            String value = row.get(columnName);
            if (value == null) {
                return;
            }

            String newValue = value.toLowerCase();
            row.set(columnName, newValue);
        };
    }

    /**
     * @see ActionMetadata#accept(ColumnMetadata)
     */
    @Override
    public boolean accept(ColumnMetadata column) {
        return Type.STRING.equals(Type.get(column.getType()));
    }
}
