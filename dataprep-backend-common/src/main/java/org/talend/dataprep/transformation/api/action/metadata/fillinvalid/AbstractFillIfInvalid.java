package org.talend.dataprep.transformation.api.action.metadata.fillinvalid;

import static org.talend.dataprep.transformation.api.action.metadata.category.ActionCategory.QUICKFIX;

import java.util.Map;
import java.util.Set;

import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;
import org.talend.dataprep.transformation.api.action.metadata.common.AbstractActionMetadata;
import org.talend.dataprep.transformation.api.action.metadata.common.ColumnAction;

public abstract class AbstractFillIfInvalid extends AbstractActionMetadata implements ColumnAction {

    public static final String DEFAULT_VALUE_PARAMETER = "invalid_default_value"; //$NON-NLS-1$

    @Override
    public String getCategory() {
        return QUICKFIX.getDisplayName();
    }

    @Override
    public void applyOnColumn(DataSetRow row, TransformationContext context, Map<String, String> parameters, String columnId) {
        final String value = row.get(columnId);
        final ColumnMetadata colMetadata = row.getRowMetadata().getById(columnId);
        final Set<String> invalidValues = colMetadata.getQuality().getInvalidValues();
        // olamy do we really consider null as a valid value?
        // note we don't for Date see @FillWithDateIfInvalid
        if (value == null) {
            return;
        }
        if (invalidValues.contains(value)) {
            row.set(columnId, parameters.get(DEFAULT_VALUE_PARAMETER));
        }
    }
}
