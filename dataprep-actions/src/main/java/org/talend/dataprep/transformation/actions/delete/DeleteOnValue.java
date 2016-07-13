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

package org.talend.dataprep.transformation.actions.delete;

import org.springframework.beans.factory.annotation.Autowired;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.parameters.Parameter;
import org.talend.dataprep.transformation.actions.common.*;
import org.talend.dataprep.transformation.api.action.context.ActionContext;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.talend.dataprep.api.type.Type.NUMERIC;
import static org.talend.dataprep.api.type.Type.STRING;
import static org.talend.dataprep.parameters.ParameterType.REGEX;

/**
 * Delete row on a given value.
 */
@DataprepAction(AbstractActionMetadata.ACTION_BEAN_PREFIX + DeleteOnValue.DELETE_ON_VALUE_ACTION_NAME)
public class DeleteOnValue extends AbstractDelete {

    /**
     * The action name.
     */
    public static final String DELETE_ON_VALUE_ACTION_NAME = "delete_on_value"; //$NON-NLS-1$

    /**
     * Name of the parameter needed.
     */
    public static final String VALUE_PARAMETER = "value"; //$NON-NLS-1$

    @Autowired
    private ReplaceOnValueHelper regexParametersHelper;

    /**
     * @see ActionMetadata#getName()
     */
    @Override
    public String getName() {
        return DELETE_ON_VALUE_ACTION_NAME;
    }

    /**
     * @see ActionMetadata#getCategory()
     */
    @Override
    @Nonnull
    public List<Parameter> getParameters() {
        final List<Parameter> parameters = super.getParameters();
        parameters.add(new Parameter(VALUE_PARAMETER, REGEX, EMPTY));
        return parameters;
    }

    /**
     * @see ActionMetadata#acceptColumn(ColumnMetadata)
     */
    @Override
    public boolean acceptColumn(ColumnMetadata column) {
        return STRING.equals(Type.get(column.getType())) || NUMERIC.isAssignableFrom(Type.get(column.getType()));
    }

    @Override
    public void compile(ActionContext actionContext) throws ActionCompileException {
        super.compile(actionContext);
        final Map<String, String> parameters = actionContext.getParameters();
        actionContext.get("replaceOnValue", p -> regexParametersHelper.build(parameters.get(VALUE_PARAMETER), true));
    }

    /**
     * @see AbstractDelete#toDelete(ActionContext, String)
     */
    @Override
    public boolean toDelete(ActionContext context, String value) {
        try {
            final ReplaceOnValueHelper helper = context.get("replaceOnValue");
            return helper.matches(value);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
