package org.talend.dataprep.transformation.api.action.metadata.text;

import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.talend.dataprep.transformation.api.action.metadata.category.ActionCategory.SPLIT;
import static org.talend.dataprep.transformation.api.action.parameters.ParameterType.INTEGER;
import static org.talend.dataprep.transformation.api.action.parameters.ParameterType.REGEX;

import java.security.InvalidParameterException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.annotation.Nonnull;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.context.ActionContext;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;
import org.talend.dataprep.transformation.api.action.metadata.common.ColumnAction;
import org.talend.dataprep.transformation.api.action.metadata.common.RegexParametersHelper;
import org.talend.dataprep.transformation.api.action.parameters.Parameter;
import org.talend.dataprep.transformation.api.action.parameters.SelectParameter;

/**
 * Split a cell value on a separator.
 */
@Component(Split.ACTION_BEAN_PREFIX + Split.SPLIT_ACTION_NAME)
public class Split extends ActionMetadata implements ColumnAction {

    @Autowired
    private RegexParametersHelper regexParametersHelper;

    /**
     * The action name.
     */
    public static final String SPLIT_ACTION_NAME = "split"; //$NON-NLS-1$

    /**
     * The split column appendix.
     */
    public static final String SPLIT_APPENDIX = "_split"; //$NON-NLS-1$

    /**
     * The separator shown to the user as a list. An item in this list is the value 'other', which allow the user to
     * manually enter its separator.
     */
    protected static final String SEPARATOR_PARAMETER = "separator"; //$NON-NLS-1$

    /**
     * The separator manually specified by the user. Should be used only if SEPARATOR_PARAMETER value is 'other'.
     */
    protected static final String MANUAL_SEPARATOR_PARAMETER = "manual_separator"; //$NON-NLS-1$

    /**
     * Number of items produces by the split
     */
    private static final String LIMIT = "limit"; //$NON-NLS-1$

    public static final String REGEX_HELPER_KEY = "regex_helper";

    /**
     * @see org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata#getName()
     */
    @Override
    public String getName() {
        return SPLIT_ACTION_NAME;
    }

    /**
     * @see org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata#getCategory()
     */
    @Override
    public String getCategory() {
        return SPLIT.getDisplayName();
    }

    @Override
    @Nonnull
    public List<Parameter> getParameters() {
        final List<Parameter> parameters = super.getParameters();
        parameters.add(new Parameter(LIMIT, INTEGER, "2"));
        //@formatter:off
        parameters.add(SelectParameter.Builder.builder()
                        .name(SEPARATOR_PARAMETER)
                        .item(":")
                        .item(";")
                        .item(",")
                        .item("@")
                        .item("-")
                        .item("_")
                        .item(" ", "<space>")
                        .item("\t", "<tab>")
                        .item("other", new Parameter(MANUAL_SEPARATOR_PARAMETER, REGEX, EMPTY))
                        .defaultValue(":")
                        .build()
        );
        //@formatter:on
        return parameters;
    }

    /**
     * @see org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata#acceptColumn(ColumnMetadata)
     */
    @Override
    public boolean acceptColumn(ColumnMetadata column) {
        return Type.STRING.equals(Type.get(column.getType()));
    }

    /**
     * @param parameters the action parameters.
     * @return the separator to use according to the given parameters.
     */
    private RegexParametersHelper.ReplaceOnValueParameter getSeparator(Map<String, String> parameters) {
        if ("other".equals(parameters.get(SEPARATOR_PARAMETER))) {
            final String jsonString = parameters.get(MANUAL_SEPARATOR_PARAMETER);
            return regexParametersHelper.build(jsonString);
        } else {
            return new RegexParametersHelper.ReplaceOnValueParameter(parameters.get(SEPARATOR_PARAMETER),
                    RegexParametersHelper.REGEX_MODE);
        }
    }

    @Override
    public void compile(ActionContext actionContext) {
        super.compile(actionContext);
        if (actionContext.getActionStatus() == ActionContext.ActionStatus.OK) {
            try {
                actionContext.get(REGEX_HELPER_KEY,(p) -> getSeparator(actionContext.getParameters()));
            } catch (InvalidParameterException e) {
                actionContext.setActionStatus(ActionContext.ActionStatus.CANCELED);
            }
        }
        actionContext.setActionStatus(ActionContext.ActionStatus.OK);
    }

    /**
     * @see ColumnAction#applyOnColumn(DataSetRow, ActionContext)
     */
    @Override
    public void applyOnColumn(DataSetRow row, ActionContext context) {
        // Retrieve the separator to use
        final RegexParametersHelper.ReplaceOnValueParameter replaceOnValueParameter = context.get(REGEX_HELPER_KEY);
        replaceOnValueParameter.setStrict(false);
        final Map<String, String> parameters = context.getParameters();
        final String columnId = context.getColumnId();
        // create the new columns
        int limit = Integer.parseInt(parameters.get(LIMIT));
        final RowMetadata rowMetadata = row.getRowMetadata();
        final ColumnMetadata column = rowMetadata.getById(columnId);
        final List<String> newColumns = new ArrayList<>();
        final Stack<String> lastColumnId = new Stack<>();
        lastColumnId.push(columnId);
        for (int i = 0; i < limit; i++) {
            newColumns.add(context.column(column.getName() + SPLIT_APPENDIX + i,
                    (r) -> {
                    final ColumnMetadata c = ColumnMetadata.Builder //
                            .column() //
                            .type(Type.STRING) //
                            .computedId(StringUtils.EMPTY) //
                            .name(column.getName() + SPLIT_APPENDIX) //
                            .build();
                    lastColumnId.push(rowMetadata.insertAfter(lastColumnId.pop(), c));
                    return c;
                }
            ));
        }

        // Set the split values in newly created columns
        final String originalValue = row.get(columnId);
        if (originalValue == null) {
            return;
        }

        if (replaceOnValueParameter.matches(originalValue)){
            final String realSeparator = replaceOnValueParameter.getToken();
            String[] split = originalValue.split(realSeparator, limit);

            final Iterator<String> iterator = newColumns.iterator();

            for (int i = 0; i < limit && iterator.hasNext(); i++) {
                final String newValue = i < split.length ? split[i] : EMPTY;
                row.set(iterator.next(), newValue);
            }
        }
    }

}
