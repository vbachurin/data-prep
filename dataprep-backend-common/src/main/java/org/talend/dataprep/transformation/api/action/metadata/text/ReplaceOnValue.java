package org.talend.dataprep.transformation.api.action.metadata.text;

import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.talend.dataprep.transformation.api.action.parameters.ParameterType.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Replace the content or part of a cell by a value.
 */
@Component(ReplaceOnValue.ACTION_BEAN_PREFIX + ReplaceOnValue.REPLACE_ON_VALUE_ACTION_NAME)
public class ReplaceOnValue extends ActionMetadata implements ColumnAction, CellAction {

    /** The dataprep ready jackson builder. */
    @Autowired
    @Lazy // needed to prevent a circular dependency
    private Jackson2ObjectMapperBuilder builder;

    /** The action name. */
    public static final String REPLACE_ON_VALUE_ACTION_NAME = "replace_on_value"; //$NON-NLS-1$

    /** Value to match. */
    public static final String CELL_VALUE_PARAMETER = "cell_value"; //$NON-NLS-1$

    /** Replace Value. */
    public static final String REPLACE_VALUE_PARAMETER = "replace_value"; //$NON-NLS-1$

    /** Scope Value (replace the entire cell or only the part that matches). */
    public static final String REPLACE_ENTIRE_CELL_PARAMETER = "replace_entire_cell"; //$NON-NLS-1$

    /** The operator parameter name. */
    protected static final String OPERATOR = "operator";

    /** The token parameter name. */
    protected static final String TOKEN = "token";

    /** The regex mode parameter name. */
    protected static final String REGEX_MODE = "regex";

    /** The value of the 'equals' operator. */
    protected static final String EQUALS_MODE = "equals";

    /** The value of the 'contains' operator. */
    protected static final String CONTAINS_MODE = "contains";

    /** The starts with parameter name. */
    protected static final String STARTS_WITH_MODE = "starts_with";

    /** The ends with parmeter name. */
    protected static final String ENDS_WITH_MODE = "ends_with";

    /** The compiled regex pattern name within the action context. */
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

    /**
     * @see ActionMetadata#compile(ActionContext)
     */
    @Override
    public void compile(ActionContext actionContext) {
        super.compile(actionContext);
        if (actionContext.getActionStatus() == ActionContext.ActionStatus.OK) {
            final Map<String, String> parameters = actionContext.getParameters();
            String value = parameters.get(CELL_VALUE_PARAMETER);

            if (value == null || value.length() == 0) {
                actionContext.setActionStatus(ActionContext.ActionStatus.CANCELED);
                return;
            }

            try {
                final ReplaceOnValueParameter replaceOnValueParameter = builder.build().readValue(value,
                        ReplaceOnValueParameter.class);
                parameters.put(OPERATOR, replaceOnValueParameter.getOperator());
                parameters.put(TOKEN, replaceOnValueParameter.getToken());
            } catch (IOException e) {
                actionContext.setActionStatus(ActionContext.ActionStatus.CANCELED);
                return;
            }

            // regex validity check
            final Boolean regexMode = (parameters.get(OPERATOR).equals(REGEX_MODE));
            if (regexMode) {
                String actualPattern = ".*" + parameters.get(TOKEN) + ".*";
                try {
                    actionContext.get(COMPILED_PATTERN, (p) -> Pattern.compile(actualPattern));
                } catch (Exception e) {
                    actionContext.setActionStatus(ActionContext.ActionStatus.CANCELED);
                }
            }
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

    /**
     * Apply the action.
     *
     * @param row the row where to apply the action.
     * @param context the action context.
     */
    private void apply(DataSetRow row, ActionContext context) {
        final String value = row.get(context.getColumnId());

        // defensive programming against null pointer exception
        if (value == null) {
            return;
        }

        final String newValue = computeNewValue(context, value);
        row.set(context.getColumnId(), newValue);
    }

    /**
     * Compute the new action based on the current action context.
     * 
     * @param context the action context.
     * @param originalValue the original value.
     * @return the new value to set based on the parameters within the action context.
     */
    protected String computeNewValue(ActionContext context, String originalValue) {
        if (originalValue == null) {
            return null;
        }

        // There are direct calls to this method from unit tests, normally such checks are done during transformation.
        if (context.getActionStatus() != ActionContext.ActionStatus.OK) {
            return originalValue;
        }

        final Map<String, String> parameters = context.getParameters();

        String operator = parameters.get(OPERATOR);
        String token = parameters.get(TOKEN);
        if (token==null || token.length()==0){
            return originalValue;
        }

        String replacement = parameters.get(REPLACE_VALUE_PARAMETER);
        boolean replaceEntireCell = Boolean.valueOf(parameters.get(REPLACE_ENTIRE_CELL_PARAMETER));

        boolean matches = false;
        switch (operator) {
        case EQUALS_MODE:
            matches = originalValue.equals(token);
            break;
        case CONTAINS_MODE:
            matches = originalValue.contains(token);
            break;
        case STARTS_WITH_MODE:
            matches = originalValue.startsWith(token);
            break;
        case ENDS_WITH_MODE:
            matches = originalValue.endsWith(token);
            break;
        case REGEX_MODE:
            final Matcher matcher = context.<Pattern>get(COMPILED_PATTERN).matcher(originalValue);
            matches = matcher.matches();
            break;
        }

        if (matches) {
            if (replaceEntireCell) {
                return replacement;
            } else {
                return originalValue.replaceAll(token, replacement);
            }
        } else {
            return originalValue;
        }
    }

    /**
     * Class used to simplify the json parsing of the parameter for this action.
     */
    public static class ReplaceOnValueParameter {

        /** The token. */
        private String token;

        /** The operator. */
        private String operator;

        /**
         * Constructor.
         * 
         * @param token the token.
         * @param operator the operator.
         */
        @JsonCreator
        public ReplaceOnValueParameter(@JsonProperty("token") String token, @JsonProperty("operator") String operator) {
            this.token = token;
            this.operator = operator;
        }

        /**
         * @return the Token
         */
        public String getToken() {
            return token;
        }

        /**
         * @return the Operator
         */
        public String getOperator() {
            return operator;
        }

    }

}
