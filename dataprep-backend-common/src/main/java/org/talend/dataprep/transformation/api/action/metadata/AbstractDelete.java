package org.talend.dataprep.transformation.api.action.metadata;

import java.util.Map;

import org.talend.dataprep.transformation.api.action.DataSetRowAction;

/**
 * Abstract class used as base class for delete actions.
 */
public abstract class AbstractDelete extends SingleColumnAction {

    /**
     * @see ActionMetadata#getCategory()
     */
    @Override
    public String getCategory() {
        return ActionCategory.CLEANSING.getDisplayName();
    }

    /**
     * @see ActionMetadata#create(Map)
     */
    @Override
    public DataSetRowAction create(Map<String, String> parameters) {
        return (row, context) -> {
            String columnId = parameters.get(COLUMN_ID);
            String value = row.get(columnId);
            if (toDelete(parameters, value)) {
                row.setDeleted(true);
            }
        };
    }

    /**
     * Return true if the given value should be deleted.
     * 
     * @param parsedParameters the delete action parameters.
     * @param value the value to delete.
     * @return true if the given value should be deleted.
     */
    public abstract boolean toDelete(Map<String, String> parsedParameters, String value);

}
