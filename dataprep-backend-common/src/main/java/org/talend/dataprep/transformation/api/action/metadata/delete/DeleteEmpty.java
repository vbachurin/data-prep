package org.talend.dataprep.transformation.api.action.metadata.delete;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;
import org.talend.dataprep.transformation.api.action.metadata.common.IColumnAction;

/**
 * Delete row when value is empty.
 */
@Component(DeleteEmpty.ACTION_BEAN_PREFIX + DeleteEmpty.DELETE_EMPTY_ACTION_NAME)
public class DeleteEmpty extends AbstractDelete implements IColumnAction {

    /**
     * The action name.
     */
    public static final String DELETE_EMPTY_ACTION_NAME = "delete_empty"; //$NON-NLS-1$

    /**
     * @see ActionMetadata#getName()
     */
    @Override
    public String getName() {
        return DELETE_EMPTY_ACTION_NAME;
    }

    /**
     * @see ActionMetadata#acceptColumn(ColumnMetadata)
     */
    @Override
    public boolean acceptColumn(ColumnMetadata column) {
        return true;
    }

    /**
     * @see AbstractDelete#toDelete(ColumnMetadata, Map, String)
     */
    @Override
    public boolean toDelete(final ColumnMetadata colMetadata, final Map<String, String> parsedParameters, final String value) {
        return value == null || value.trim().length() == 0;
    }
}
