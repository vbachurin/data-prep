package org.talend.dataprep.transformation.api.action.metadata.text;

import static org.apache.commons.lang.BooleanUtils.toStringTrueFalse;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.talend.dataprep.api.type.Type.BOOLEAN;
import static org.talend.dataprep.api.type.Type.STRING;
import static org.talend.dataprep.transformation.api.action.parameters.ParameterType.REGEX;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.annotation.Nonnull;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.context.ActionContext;
import org.talend.dataprep.transformation.api.action.metadata.category.ActionCategory;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;
import org.talend.dataprep.transformation.api.action.metadata.common.ColumnAction;
import org.talend.dataprep.transformation.api.action.parameters.Parameter;
import org.talend.dataprep.transformation.api.action.parameters.SelectParameter;

@Component(MatchesPattern.ACTION_BEAN_PREFIX + MatchesPattern.MATCHES_PATTERN_ACTION_NAME)
public class MatchesPattern extends ActionMetadata implements ColumnAction {

    /**
     * The action name.
     */
    public static final String MATCHES_PATTERN_ACTION_NAME = "matches_pattern"; //$NON-NLS-1$

    /**
     * The column appendix.
     */
    public static final String APPENDIX = "_matching"; //$NON-NLS-1$

    /**
     * The pattern shown to the user as a list. An item in this list is the value 'other', which allow the user to
     * manually enter his pattern.
     */
    public static final String PATTERN_PARAMETER = "proposed_pattern"; //$NON-NLS-1$

    /**
     * The pattern manually specified by the user. Should be used only if PATTERN_PARAMETER value is 'other'.
     */
    private static final String MANUAL_PATTERN_PARAMETER = "manual_pattern"; //$NON-NLS-1$

    public static final String CUSTOM = "custom";

    public static final String COMPILED_PATTERN = "compiled_pattern";

    /**
     * @see ActionMetadata#getName()
     */
    @Override
    public String getName() {
        return MATCHES_PATTERN_ACTION_NAME;
    }

    /**
     * @see ActionMetadata#acceptColumn(ColumnMetadata)
     */
    @Override
    public boolean acceptColumn(ColumnMetadata column) {
        return STRING.equals(Type.get(column.getType()));
    }

    /**
     * @see ActionMetadata#getCategory()
     */
    @Override
    public String getCategory() {
        return ActionCategory.STRINGS.getDisplayName();
    }

    @Override
    @Nonnull
    public List<Parameter> getParameters() {
        final List<Parameter> parameters = super.getParameters();
        // @formatter:off
		parameters.add(SelectParameter.Builder.builder()
				.name(PATTERN_PARAMETER)
				.item("[a-z]+", "[a-z]+ (a word in lowercase)")
				.item("[A-Z]+", "[A-Z]+ (a word in uppercase)")
				.item("[0-9]+", "[0-9]+ (number)")
				.item("[a-zA-Z]+", "[a-zA-Z]+ (any word, case insensitive)")
				.item("[a-zA-Z0-9]+", "[a-zA-Z0-9]+ (any literal with alpha-num characters)")
				.item(CUSTOM, "other (design your custom pattern)", new Parameter(MANUAL_PATTERN_PARAMETER, REGEX, EMPTY))
				.defaultValue("[a-zA-Z]+")
				.build());
		// @formatter:on
        return parameters;
    }

    /**
     * @param parameters the action parameters.
     * @return the pattern to use according to the given parameters.
     */
    private String getPattern(Map<String, String> parameters) {
        return (CUSTOM).equals(parameters.get(PATTERN_PARAMETER)) ? parameters.get(MANUAL_PATTERN_PARAMETER)
                : parameters.get(PATTERN_PARAMETER);
    }

    @Override
    public void compile(ActionContext actionContext) {
        super.compile(actionContext);
        if (actionContext.getActionStatus() == ActionContext.ActionStatus.OK) {
            final String pattern = getPattern(actionContext.getParameters());
            if (pattern == null || pattern.length() < 1) {
                // In case of empty pattern, consider that value does not match:
                actionContext.setActionStatus(ActionContext.ActionStatus.CANCELED);
                return;
            }
            try {
                actionContext.get(COMPILED_PATTERN, (p) -> Pattern.compile(pattern));
            } catch (PatternSyntaxException e) {
                // In case of wrong pattern, consider that value does not match:
                actionContext.setActionStatus(ActionContext.ActionStatus.CANCELED);
            }
        }
    }

    /**
     * @see ColumnAction#applyOnColumn(DataSetRow, ActionContext)
     */
    @Override
    public void applyOnColumn(DataSetRow row, ActionContext context) {
        // Retrieve the pattern to use
        final String columnId = context.getColumnId();

        // create new column and append it after current column
        final RowMetadata rowMetadata = row.getRowMetadata();
        final ColumnMetadata column = rowMetadata.getById(columnId);
        // rowMetadata.insertAfter(columnId, newCol)
        final String matchingColumn = context.column(column.getName() + APPENDIX, (r) -> {
            final ColumnMetadata c = ColumnMetadata.Builder //
                    .column() //
                    .name(column.getName() + APPENDIX) //
                    .type(BOOLEAN) //
                    .empty(column.getQuality().getEmpty()) //
                    .invalid(column.getQuality().getInvalid()) //
                    .valid(column.getQuality().getValid()) //
                    .headerSize(column.getHeaderSize()) //
                    .build();
            rowMetadata.insertAfter(columnId, c);
            return c;
        });

        final String value = row.get(columnId);
        final String newValue = toStringTrueFalse(computeNewValue(value, context));
        row.set(matchingColumn, newValue);
    }

    /**
     * Computes if a given string matches or not given pattern.
     *
     * @param value the value to test
     * @param actionContext context expected to contain the compiled pattern to match the value against.
     * @return true if 'value' matches 'pattern', false if not or if 'pattern' is not a valid pattern or is null or empty
     */
    protected boolean computeNewValue(String value, ActionContext actionContext) {
        // There are direct calls to this method from unit tests, normally such checks are done during transformation.
        if (actionContext.getActionStatus() != ActionContext.ActionStatus.OK) {
            return false;
        }
        final Pattern p = actionContext.get(COMPILED_PATTERN);
        final Matcher matcher = p.matcher(value == null ? StringUtils.EMPTY : value);
        return matcher.matches();
    }

}
