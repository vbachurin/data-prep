package org.talend.dataprep.transformation.api.action.metadata.text;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;
import org.talend.dataprep.transformation.api.action.metadata.category.ActionCategory;
import org.talend.dataprep.transformation.api.action.metadata.common.AbstractActionMetadata;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;
import org.talend.dataprep.transformation.api.action.metadata.common.IColumnAction;

/**
 * Uppercase a column in a row.
 */
@Component(UpperCase.ACTION_BEAN_PREFIX + UpperCase.UPPER_CASE_ACTION_NAME)
public class UpperCase extends AbstractActionMetadata implements IColumnAction {

    /**
     * The action code name.
     */
    public static final String UPPER_CASE_ACTION_NAME = "uppercase"; //$NON-NLS-1$

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
        return ActionCategory.CASE.getDisplayName();
    }

    /**
     * @see ActionMetadata#acceptColumn(ColumnMetadata)
     */
    @Override
    public boolean acceptColumn(ColumnMetadata column) {
        return Type.STRING.equals(Type.get(column.getType()));
    }

    @Override
    protected void beforeApply(Map<String, String> parameters) {
    }

    @Override
    public void applyOnColumn(DataSetRow row, TransformationContext context, Map<String, String> parameters, String columnId) {
        final String value = row.get(columnId);
        if (value != null) {
            row.set(columnId, value.toUpperCase());
        }
    }
}
