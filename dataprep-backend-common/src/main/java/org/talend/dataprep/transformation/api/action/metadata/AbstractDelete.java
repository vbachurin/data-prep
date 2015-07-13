package org.talend.dataprep.transformation.api.action.metadata;

import static org.talend.dataprep.api.preparation.Action.Builder.builder;

import java.util.Map;

import org.talend.dataprep.api.preparation.Action;

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
    public Action create(Map<String, String> parameters) {
        return builder().withRow((row, context) -> {
            String columnId = parameters.get(COLUMN_ID);
            String value = row.get(columnId);
            if (toDelete(parameters, value)) {
                row.setDeleted(true);
            }
        }).build();
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
