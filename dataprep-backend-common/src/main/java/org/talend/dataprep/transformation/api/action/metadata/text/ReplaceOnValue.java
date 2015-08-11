package org.talend.dataprep.transformation.api.action.metadata.text;

import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.talend.dataprep.api.type.Type.STRING;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;
import org.talend.dataprep.transformation.api.action.metadata.category.ActionCategory;
import org.talend.dataprep.transformation.api.action.metadata.common.AbstractActionMetadata;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;
import org.talend.dataprep.transformation.api.action.metadata.common.ICellAction;
import org.talend.dataprep.transformation.api.action.metadata.common.IColumnAction;
import org.talend.dataprep.transformation.api.action.parameters.Parameter;

@Component(ReplaceOnValue.ACTION_BEAN_PREFIX + ReplaceOnValue.REPLACE_ON_VALUE_ACTION_NAME)
public class ReplaceOnValue extends AbstractActionMetadata implements IColumnAction, ICellAction {

    /**
     * The action name.
     */
    public static final String REPLACE_ON_VALUE_ACTION_NAME = "replace_on_value"; //$NON-NLS-1$

    /**
     * Value to match
     */
    public static final String CELL_VALUE_PARAMETER = "cell_value"; //$NON-NLS-1$

    /**
     * Replace Value
     */
    public static final String REPLACE_VALUE_PARAMETER = "replace_value"; //$NON-NLS-1$

    /**
     * @see ActionMetadata#getName()
     */
    @Override
    public String getName() {
        return REPLACE_ON_VALUE_ACTION_NAME;
    }

    /**
     * @see ActionMetadata#getCategory()
     */
    @Override
    public String getCategory() {
        return ActionCategory.QUICKFIX.getDisplayName();
    }

    /**
     * @see ActionMetadata#getCategory()
     */
    @Override
    public List<Parameter> getParameters() {
        final List<Parameter> parameters = super.getParameters();
        parameters.add(new Parameter(CELL_VALUE_PARAMETER, STRING.getName(), EMPTY));
        parameters.add(new Parameter(REPLACE_VALUE_PARAMETER, STRING.getName(), EMPTY));
        return parameters;
    }

    /**
     * @see ActionMetadata#acceptColumn(ColumnMetadata)
     */
    @Override
    public boolean acceptColumn(ColumnMetadata column) {
        return STRING.equals(Type.get(column.getType()));
    }

    @Override
    protected void beforeApply(Map<String, String> parameters) {
    }

    private void apply(DataSetRow row, Map<String, String> parameters, String columnId) {
        final String value = row.get(columnId);
        final String toMatch = parameters.get(CELL_VALUE_PARAMETER);

        if (toMatch.equals(value)) {
            final String toReplace = parameters.get(REPLACE_VALUE_PARAMETER);
            row.set(columnId, toReplace);
        }
    }

    @Override
    public void applyOnColumn(DataSetRow row, TransformationContext context, Map<String, String> parameters, String columnId) {
        apply(row, parameters, columnId);
    }

    @Override
    public void applyOnCell(DataSetRow row, TransformationContext context, Map<String, String> parameters, Long rowId, String columnId) {
        apply(row, parameters, columnId);
    }
}
