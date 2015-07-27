package org.talend.dataprep.transformation.api.action.metadata.fillempty;

import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;
import org.talend.dataprep.transformation.api.action.metadata.category.ActionCategory;
import org.talend.dataprep.transformation.api.action.metadata.common.AbstractActionMetadata;
import org.talend.dataprep.transformation.api.action.metadata.common.IColumnAction;

import java.util.Map;

import static org.talend.dataprep.transformation.api.action.metadata.category.ActionCategory.QUICKFIX;

public abstract class AbstractFillIfEmpty extends AbstractActionMetadata implements IColumnAction {

    public static final String DEFAULT_VALUE_PARAMETER = "default_value"; //$NON-NLS-1$

    @Override
    public String getCategory() {
        return QUICKFIX.getDisplayName();
    }

    @Override
    protected void beforeApply(Map<String, String> parameters) {
    }

    @Override
    public void applyOnColumn(DataSetRow row, TransformationContext context, Map<String, String> parameters, String columnId) {
        final String value = row.get(columnId);
        if (value == null || value.trim().length() == 0) {
            row.set(columnId, parameters.get(DEFAULT_VALUE_PARAMETER));
        }
    }
}
