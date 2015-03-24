package org.talend.dataprep.transformation.api.action.metadata;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Bean;
import org.talend.dataprep.api.type.Types;

public class DeleteOnValue extends AbstractDelete {

    public static final String DELETE_ON_VALUE_ACTION_NAME = "delete_on_value"; //$NON-NLS-1$

    public static final String VALUE_PARAMETER = "value"; //$NON-NLS-1$

    public static final ActionMetadata INSTANCE = new DeleteOnValue();

    @Bean(name = ACTION_BEAN_PREFIX + DELETE_ON_VALUE_ACTION_NAME)
    public ActionMetadata createInstance() {
        return new DeleteOnValue();
    }

    // Please do not instanciate this class, it is spring Bean automatically instanciated.
    public DeleteOnValue() {
    }

    @Override
    public String getName() {
        return DELETE_ON_VALUE_ACTION_NAME;
    }

    @Override
    public Parameter[] getParameters() {
        return new Parameter[] { new Parameter(COLUMN_NAME_PARAMETER, Types.STRING.getName(), StringUtils.EMPTY),
                new Parameter(VALUE_PARAMETER, Types.STRING.getName(), StringUtils.EMPTY) };
    }

    @Override
    public boolean toDelete(Map<String, String> parsedParameters, String value) {
        return (value != null && value.trim().equals(parsedParameters.get(VALUE_PARAMETER)));
    }

}
