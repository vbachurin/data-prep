// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// https://github.com/Talend/data-prep/blob/master/LICENSE
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package org.talend.dataprep.transformation.actions.text;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.talend.dataprep.api.action.Action;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.row.DataSetRow;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.i18n.ActionsBundle;
import org.talend.dataprep.parameters.Parameter;
import org.talend.dataprep.parameters.ParameterType;
import org.talend.dataprep.parameters.SelectParameter;
import org.talend.dataprep.transformation.actions.category.ActionCategory;
import org.talend.dataprep.transformation.actions.common.AbstractActionMetadata;
import org.talend.dataprep.transformation.actions.common.ColumnAction;
import org.talend.dataprep.transformation.api.action.context.ActionContext;
import org.talend.dataquality.converters.StringConverter;

/**
 * Trim leading and trailing characters.
 */
@Action(AbstractActionMetadata.ACTION_BEAN_PREFIX + Trim.TRIM_ACTION_NAME)
public class Trim extends AbstractActionMetadata implements ColumnAction {

    /**
     * The action name.
     */
    public static final String TRIM_ACTION_NAME = "trim"; //$NON-NLS-1$

    /** Padding Character. */
    public static final String PADDING_CHAR_PARAMETER = "padding_character"; //$NON-NLS-1$

    /** Custom Padding Character. */
    public static final String CUSTOM_PADDING_CHAR_PARAMETER = "custom_padding_character"; //$NON-NLS-1$

    /** String Converter help class. */
    public static final String STRING_CONVERTRT = "string_converter"; //$NON-NLS-1$

    /**
     * Keys used in the values of different parameters:
     */
    public static final String CUSTOM = "custom"; //$NON-NLS-1$

    @Override
    public String getName() {
        return TRIM_ACTION_NAME;
    }

    @Override
    public String getCategory() {
        return ActionCategory.STRINGS.getDisplayName();
    }

    @Override
    public boolean acceptField(ColumnMetadata column) {
        return Type.STRING.equals(Type.get(column.getType()));
    }

    @Override
    public List<Parameter> getParameters() {
        final List<Parameter> parameters = super.getParameters();

        // @formatter:off
        parameters.add(SelectParameter.Builder.builder()
                .name(PADDING_CHAR_PARAMETER)
                .item("whitespace", " ") //$NON-NLS-1$
                .item("character_tabulation"       , "\\u0009") //$NON-NLS-1$ //$NON-NLS-2$
                .item("line_feed_(lf)"             , "\\u000A") //$NON-NLS-1$ //$NON-NLS-2$
                .item("line_tabulation"            , "\\u000B") //$NON-NLS-1$ //$NON-NLS-2$
                .item("form_feed_(ff)"             , "\\u000C") //$NON-NLS-1$ //$NON-NLS-2$
                .item("carriage_return_(cr)"       , "\\u000D") //$NON-NLS-1$ //$NON-NLS-2$
                .item("next_line_(nel)"            , "\\u0085") //$NON-NLS-1$ //$NON-NLS-2$
                .item("no_break_space"             , "\\u00A0") //$NON-NLS-1$ //$NON-NLS-2$
                .item("ogham_space_mark"           , "\\u1680") //$NON-NLS-1$ //$NON-NLS-2$
                .item("mongolian_vowel_separator"  , "\\u180E") //$NON-NLS-1$ //$NON-NLS-2$
                .item("en_quad"                    , "\\u2000") //$NON-NLS-1$ //$NON-NLS-2$
                .item("em_quad"                    , "\\u2001") //$NON-NLS-1$ //$NON-NLS-2$
                .item("en_space"                   , "\\u2002") //$NON-NLS-1$ //$NON-NLS-2$
                .item("em_space"                   , "\\u2003") //$NON-NLS-1$ //$NON-NLS-2$
                .item("three_per_em_space"         , "\\u2004") //$NON-NLS-1$ //$NON-NLS-2$
                .item("four_per_em_space"          , "\\u2005") //$NON-NLS-1$ //$NON-NLS-2$
                .item("six_per_em_space"           , "\\u2006") //$NON-NLS-1$ //$NON-NLS-2$
                .item("figure_space"               , "\\u2007") //$NON-NLS-1$ //$NON-NLS-2$
                .item("punctuation_space"          , "\\u2008") //$NON-NLS-1$ //$NON-NLS-2$
                .item("thin_space"                 , "\\u2009") //$NON-NLS-1$ //$NON-NLS-2$
                .item("hair_space"                 , "\\u200A") //$NON-NLS-1$ //$NON-NLS-2$
                .item("line_separator"             , "\\u2028") //$NON-NLS-1$ //$NON-NLS-2$
                .item("paragraph_separator"        , "\\u2029") //$NON-NLS-1$ //$NON-NLS-2$
                .item("narrow_no_break_space"      , "\\u202F") //$NON-NLS-1$ //$NON-NLS-2$
                .item("medium_mathematical_space"  , "\\u205F") //$NON-NLS-1$ //$NON-NLS-2$
                .item("ideographic_space"          , "\\u3000") //$NON-NLS-1$ //$NON-NLS-2$
                .item(CUSTOM, CUSTOM,new Parameter(CUSTOM_PADDING_CHAR_PARAMETER, ParameterType.STRING, StringUtils.EMPTY)) 
                .defaultValue(" ")
                .build());
        // @formatter:on
        return ActionsBundle.attachToAction(parameters, this);
    }

    @Override
    public void compile(ActionContext actionContext) {
        super.compile(actionContext);
        if (actionContext.getActionStatus() == ActionContext.ActionStatus.OK) {
            actionContext.get(STRING_CONVERTRT, p -> new StringConverter());
        }
    }

    /**
     * @see ColumnAction#applyOnColumn(DataSetRow, ActionContext)
     */
    @Override
    public void applyOnColumn(DataSetRow row, ActionContext context) {
        final String columnId = context.getColumnId();
        final String toTrim = row.get(columnId);
        if (toTrim != null) {
            final Map<String, String> parameters = context.getParameters();
            String removeChar;
            if (CUSTOM.equals(parameters.get(PADDING_CHAR_PARAMETER))) {
                removeChar = parameters.get(CUSTOM_PADDING_CHAR_PARAMETER);
            } else {
                removeChar = parameters.get(PADDING_CHAR_PARAMETER);
            }
            final StringConverter stringConverter = context.get(STRING_CONVERTRT);
            row.set(columnId, stringConverter.removeTrailingAndLeading(toTrim, removeChar));
        }
    }

    @Override
    public Set<Behavior> getBehavior() {
        return EnumSet.of(Behavior.VALUES_COLUMN);
    }
}
