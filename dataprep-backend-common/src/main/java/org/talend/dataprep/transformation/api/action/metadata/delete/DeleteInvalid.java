package org.talend.dataprep.transformation.api.action.metadata.delete;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;
import org.talend.dataprep.transformation.api.action.metadata.common.IColumnAction;

/**
 * Delete row when value is invalid.
 */
@Component(DeleteInvalid.ACTION_BEAN_PREFIX + DeleteInvalid.DELETE_INVALID_ACTION_NAME)
public class DeleteInvalid extends AbstractDelete implements IColumnAction {

    /**
     * The action name.
     */
    public static final String DELETE_INVALID_ACTION_NAME = "delete_invalid"; //$NON-NLS-1$

    /**
     * @see ActionMetadata#getName()
     */
    @Override
    public String getName() {
        return DELETE_INVALID_ACTION_NAME;
    }

    /**
     * @see AbstractDelete#toDelete(Map, String)
     */
    @Override
    public boolean toDelete(Map<String, String> parsedParameters, String value) {
        return value == null || value.trim().length() == 0;
    }

    @Override
    public void applyOnColumn( DataSetRow row, TransformationContext context, Map<String, String> parameters,
                               String columnId )
    {
        super.applyOnColumn( row, context, parameters, columnId );
    }

    /**
     * @see ActionMetadata#acceptColumn(ColumnMetadata)
     */
    @Override
    public boolean acceptColumn(ColumnMetadata column) {
        return true;
    }
}
