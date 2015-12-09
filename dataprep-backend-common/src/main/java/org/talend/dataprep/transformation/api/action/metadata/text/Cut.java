package org.talend.dataprep.transformation.api.action.metadata.text;

import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.talend.dataprep.api.type.Type.STRING;
import static org.talend.dataprep.transformation.api.action.metadata.category.ActionCategory.STRINGS;
import static org.talend.dataprep.transformation.api.action.parameters.ParameterType.REGEX;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.context.ActionContext;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;
import org.talend.dataprep.transformation.api.action.metadata.common.ColumnAction;
import org.talend.dataprep.transformation.api.action.parameters.Parameter;

@Component(Cut.ACTION_BEAN_PREFIX + Cut.CUT_ACTION_NAME)
public class Cut extends ActionMetadata implements ColumnAction {

    /**
     * The action name.
     */
    public static final String CUT_ACTION_NAME = "cut"; //$NON-NLS-1$

    /**
     * The pattern "where to cut" parameter name
     */
    public static final String PATTERN_PARAMETER = "pattern"; //$NON-NLS-1$

    /**
     * The compiled pattern based on provided value.
     */
    private static final String COMPILED_PATTERN = "compiled_pattern"; //$NON-NLS-1$

    /**
     * @see ActionMetadata#getName()
     */
    @Override
    public String getName() {
        return CUT_ACTION_NAME;
    }

    /**
     * @see ActionMetadata#getCategory()
     */
    @Override
    public String getCategory() {
        return STRINGS.getDisplayName();
    }

    /**
     * @see ActionMetadata#getParameters()
     */
    @Override
    public List<Parameter> getParameters() {
        final List<Parameter> parameters = super.getParameters();
        parameters.add(new Parameter(PATTERN_PARAMETER, REGEX, EMPTY));
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
    public void compile(ActionContext actionContext) {
        try {
            Pattern.compile(actionContext.getParameters().get(PATTERN_PARAMETER));
            actionContext.setActionStatus(ActionContext.ActionStatus.OK);
        } catch (Exception e) {
            actionContext.setActionStatus(ActionContext.ActionStatus.CANCELED);
        }
    }

    /**
     * @see ColumnAction#applyOnColumn(DataSetRow, ActionContext)
     */
    @Override
    public void applyOnColumn(DataSetRow row, ActionContext context) {
        final String columnId = context.getColumnId();
        final String toCut = row.get(columnId);
        if (toCut != null) {
            try {
                // Check if the pattern is valid:
                Pattern p = context.get(COMPILED_PATTERN, (map) -> Pattern.compile(map.get(PATTERN_PARAMETER)));
                row.set(columnId, p.matcher(toCut).replaceAll("")); //$NON-NLS-1$
            } catch (PatternSyntaxException e) {
                // In case the pattern is not valid, consider that the value does not match: do nothing.
            }
        }
    }
}
