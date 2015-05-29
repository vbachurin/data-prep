package org.talend.dataprep.transformation.api.action.metadata;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.parameters.Parameter;

/**
 * Delete row on a given value.
 */
@Component(DeleteOnValue.ACTION_BEAN_PREFIX + DeleteOnValue.DELETE_ON_VALUE_ACTION_NAME)
public class DeleteOnValue extends AbstractDelete {

    /** The action name. */
    public static final String DELETE_ON_VALUE_ACTION_NAME = "delete_on_value"; //$NON-NLS-1$

    /** Name of the parameter needed. */
    public static final String VALUE_PARAMETER = "value"; //$NON-NLS-1$

    /**
     * Private constructor to ensure IoC.
     */
    private DeleteOnValue() {
    }

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
    public Parameter[] getParameters() {
        //@formatter:off
        return new Parameter[] {COLUMN_ID_PARAMETER,
                                COLUMN_NAME_PARAMETER,
                                new Parameter(VALUE_PARAMETER, Type.STRING.getName(), StringUtils.EMPTY)};
        //@formatter:on
    }

    /**
     * @see AbstractDelete#toDelete(Map, String)
     */
    @Override
    public boolean toDelete(Map<String, String> parsedParameters, String value) {
        return (value != null && value.trim().equals(parsedParameters.get(VALUE_PARAMETER)));
    }

    /**
     * @see ActionMetadata#getCompatibleColumnTypes()
     */
    @Override
    public Set<Type> getCompatibleColumnTypes() {
        Set<Type> toReturn = new HashSet<>();
        toReturn.add(Type.STRING);
        toReturn.add(Type.NUMERIC);
        return toReturn;
    }

}
