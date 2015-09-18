package org.talend.dataprep.transformation.api.action.metadata.fillempty;

import static org.talend.dataprep.transformation.api.action.metadata.category.ActionCategory.DATA_CLEANSING;

import java.util.Map;

import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;
import org.talend.dataprep.transformation.api.action.metadata.common.AbstractActionMetadata;
import org.talend.dataprep.transformation.api.action.metadata.common.ColumnAction;

public abstract class AbstractFillIfEmpty extends AbstractActionMetadata implements ColumnAction {

    public static final String DEFAULT_VALUE_PARAMETER = "empty_default_value"; //$NON-NLS-1$

    @Override
    public String getCategory() {
        return DATA_CLEANSING.getDisplayName();
    }


    /**
     * @see ColumnAction#applyOnColumn(DataSetRow, TransformationContext, Map, String)
     */
    @Override
    public void applyOnColumn(DataSetRow row, TransformationContext context, Map<String, String> parameters, String columnId) {
        final String value = row.get(columnId);
        if (value == null || value.trim().length() == 0) {
            row.set(columnId, parameters.get(DEFAULT_VALUE_PARAMETER));
        }
    }
}
