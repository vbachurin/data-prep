package org.talend.dataprep.transformation.api.action.metadata;

import java.util.Map;

public class DeleteEmpty extends AbstractDelete {

    public static final String         DELETE_EMPTY_ACTION_NAME = "delete_empty";                         //$NON-NLS-1$

    public static final String         DELETE_EMPTY_ACTION_DESC = "Delete rows that have this cell empty"; //$NON-NLS-1$

    public static final ActionMetadata INSTANCE                 = new DeleteEmpty();

    private DeleteEmpty() {
    }

    @Override
    public String getName() {
        return DELETE_EMPTY_ACTION_NAME;
    }

    @Override
    public String getDescription() {
        return DELETE_EMPTY_ACTION_DESC;
    }

    @Override
    public boolean toDelete(Map<String, String> parsedParameters, String value) {
        return (value == null || value.trim().length() == 0);
    }

}
