package org.talend.dataprep.transformation.api.action.metadata;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.parameters.Item;

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
     * @see ActionMetadata#getItems()
     */
    @Override
    @Nonnull
    public Item[] getItems() {
        return new Item[0];
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
     * @see ActionMetadata#getCompatibleColumnTypes()
     */
    @Override
    public Set<Type> getCompatibleColumnTypes() {
        return Collections.singleton(Type.STRING);
    }
}
