package org.talend.dataprep.transformation.api.action.metadata.delete;

import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;
import org.talend.dataprep.transformation.api.action.metadata.common.AbstractActionMetadata;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;
import org.talend.dataprep.transformation.api.action.metadata.common.IColumnAction;

import static org.talend.dataprep.transformation.api.action.metadata.category.ActionCategory.CLEANSING;

/**
 * Delete row when value is invalid.
 */
@Component(DeleteInvalid.ACTION_BEAN_PREFIX + DeleteInvalid.DELETE_INVALID_ACTION_NAME)
public class DeleteInvalid extends AbstractActionMetadata
    implements IColumnAction {

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

    @Override
    protected void beforeApply(Map<String, String> parameters) {
        // no op
    }

    /**
     * @see ActionMetadata#getCategory()
     */
    @Override
    public String getCategory() {
        return CLEANSING.getDisplayName();
    }

    @Override
    public void applyOnColumn(DataSetRow row, TransformationContext context, Map<String, String> parameters, String columnId) {
        final String value = row.get(columnId);
        
        ColumnMetadata columnMetadata = row.getRowMetadata().getById( columnId );
        if (columnMetadata == null){
            return;
        }
        if (columnMetadata.getQuality()==null){
            return;
        }

        Set<String> invalidValues = row.getRowMetadata().getById( columnId ).getQuality().getInvalidValues();
        if (invalidValues != null && invalidValues.contains( value )){
            row.setDeleted( true );
        }

    }

    /**
     * @see ActionMetadata#acceptColumn(ColumnMetadata)
     */
    @Override
    public boolean acceptColumn(ColumnMetadata column) {
        return true;
    }
}
