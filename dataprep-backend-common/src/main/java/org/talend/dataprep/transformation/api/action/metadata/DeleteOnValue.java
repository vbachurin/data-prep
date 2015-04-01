package org.talend.dataprep.transformation.api.action.metadata;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.type.Type;

@Component(DeleteOnValue.ACTION_BEAN_PREFIX + DeleteOnValue.DELETE_ON_VALUE_ACTION_NAME)
public class DeleteOnValue extends AbstractDelete {

    public static final String DELETE_ON_VALUE_ACTION_NAME = "delete_on_value"; //$NON-NLS-1$

    public static final String VALUE_PARAMETER             = "value";          //$NON-NLS-1$

    private DeleteOnValue() {
    }

    @Override
    public String getName() {
        return DELETE_ON_VALUE_ACTION_NAME;
    }

    @Override
    @Nonnull
    public Parameter[] getParameters() {
        return new Parameter[] { new Parameter(COLUMN_NAME_PARAMETER, Type.STRING.getName(), StringUtils.EMPTY),
                new Parameter(VALUE_PARAMETER, Type.STRING.getName(), StringUtils.EMPTY) };
    }

    @Override
    public boolean toDelete(Map<String, String> parsedParameters, String value) {
        return (value != null && value.trim().equals(parsedParameters.get(VALUE_PARAMETER)));
    }

    @Override
    public Set<Type> getCompatibleColumnTypes() {
        return Collections.singleton(Type.STRING);
    }

}
