package org.talend.dataprep.transformation.api.action.metadata;

import static org.talend.dataprep.api.dataset.DataSetRowWithDiff.FLAG.UPDATE;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.DataSetRowWithDiff;
import org.talend.dataprep.api.type.Type;

/**
 * Uppercase a column in a row.
 */
@Component(UpperCase.ACTION_BEAN_PREFIX + UpperCase.UPPER_CASE_ACTION_NAME)
public class UpperCase extends SingleColumnAction {

    /** The action code name. */
    public static final String UPPER_CASE_ACTION_NAME = "uppercase"; //$NON-NLS-1$

    /**
     * Private constructor to ensure IoC.
     */
    private UpperCase() {
    }

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
            String columnName = parameters.get(COLUMN_NAME_PARAMETER_NAME);
            String value = row.get(columnName);
            if (value == null) {
                return;
            }

            String newValue = value.toUpperCase();
            row.set(columnName, newValue);

            // compute the diff if needed
            if (!(row instanceof DataSetRowWithDiff)) {
                return;
            }

            DataSetRowWithDiff rowWithDiff = (DataSetRowWithDiff) row;
            String originalValue = rowWithDiff.getReference().get(columnName);

            // upper case action can only update value, no need to worry about new or deleted flags, they're taken
            // cared of by other actions

            if (StringUtils.equals(originalValue, newValue)) {
                rowWithDiff.clearFlag(columnName);
            } else {
                rowWithDiff.setFlag(UPDATE, columnName);
            }

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
