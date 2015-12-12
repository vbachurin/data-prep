package org.talend.dataprep.transformation.api.action.metadata.text;

import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.talend.dataprep.transformation.api.action.parameters.ParameterType.*;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.context.ActionContext;
import org.talend.dataprep.transformation.api.action.metadata.category.ActionCategory;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;
import org.talend.dataprep.transformation.api.action.metadata.common.CellAction;
import org.talend.dataprep.transformation.api.action.metadata.common.ColumnAction;
import org.talend.dataprep.transformation.api.action.parameters.Parameter;

@Component(ReplaceOnValue.ACTION_BEAN_PREFIX + ReplaceOnValue.REPLACE_ON_VALUE_ACTION_NAME)
public class ReplaceOnValue extends ActionMetadata implements ColumnAction, CellAction {

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
     * Scope Value
     */
    public static final String REPLACE_ENTIRE_CELL_PARAMETER = "replace_entire_cell"; //$NON-NLS-1$

    private static final String COMPILED_PATTERN = "compiled_pattern";

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
        return ActionCategory.STRINGS.getDisplayName();
    }

    /**
     * @see ActionMetadata#getCategory()
     */
    @Override
    public List<Parameter> getParameters() {
        final List<Parameter> parameters = super.getParameters();
        parameters.add(new Parameter(CELL_VALUE_PARAMETER, REGEX, EMPTY));
        parameters.add(new Parameter(REPLACE_VALUE_PARAMETER, STRING, EMPTY));
        parameters.add(new Parameter(REPLACE_ENTIRE_CELL_PARAMETER, BOOLEAN, "false"));
        return parameters;
    }

    /**
     * @see ActionMetadata#acceptColumn(ColumnMetadata)
     */
    @Override
    public boolean acceptColumn(ColumnMetadata column) {
        return Type.STRING.equals(Type.get(column.getType()));
    }

    @Override
    public void compile(ActionContext actionContext) {
        super.compile(actionContext);
        if (actionContext.getActionStatus() == ActionContext.ActionStatus.OK) {
            final Map<String, String> parameters = actionContext.getParameters();
            String regexp = parameters.get(CELL_VALUE_PARAMETER);
            if (regexp == null || regexp.length() == 0) {
                actionContext.setActionStatus(ActionContext.ActionStatus.CANCELED);
                return;
            }
            final Boolean replaceEntireCell = Boolean.valueOf(parameters.get(REPLACE_ENTIRE_CELL_PARAMETER));
            if (replaceEntireCell) {
                regexp = ".*" + regexp + ".*";
            }
            final String actualPattern = regexp;
            // regex validity check
            try {
                actionContext.get(COMPILED_PATTERN, (p) -> Pattern.compile(actualPattern));
            } catch (Exception e) {
                actionContext.setActionStatus(ActionContext.ActionStatus.CANCELED);
            }
        }
    }

    private void apply(DataSetRow row, ActionContext context) {
        final String value = row.get(context.getColumnId());

        // defensive programming against null pointer exception
        if (value == null) {
            return;
        }

        final String newValue = computeNewValue(context, value);
        row.set(context.getColumnId(), newValue);
    }

    protected String computeNewValue(ActionContext context, String originalValue) {
        if (originalValue == null) {
            return null;
        }
        // There are direct calls to this method from unit tests, normally such checks are done during transformation.
        if (context.getActionStatus() != ActionContext.ActionStatus.OK) {
            return originalValue;
        }
        final Map<String, String> parameters = context.getParameters();
        String replacement = parameters.get(REPLACE_VALUE_PARAMETER);
        boolean replaceEntireCell = Boolean.valueOf(parameters.get(REPLACE_ENTIRE_CELL_PARAMETER));
        final Matcher matcher = context.<Pattern>get(COMPILED_PATTERN).matcher(originalValue);
        if (replaceEntireCell) {
            if (matcher.matches()) {
                return replacement;
            } else {
                return originalValue;
            }
        } else {
            return matcher.replaceAll(replacement);
        }
    }

    /**
     * @see ColumnAction#applyOnColumn(DataSetRow, ActionContext)
     */
    @Override
    public void applyOnColumn(DataSetRow row, ActionContext context) {
        apply(row, context);
    }

    /**
     * @see CellAction#applyOnCell(DataSetRow, ActionContext)
     */
    @Override
    public void applyOnCell(DataSetRow row, ActionContext context) {
        apply(row, context);
    }
}
