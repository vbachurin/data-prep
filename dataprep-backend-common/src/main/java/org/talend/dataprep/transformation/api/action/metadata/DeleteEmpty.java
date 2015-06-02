package org.talend.dataprep.transformation.api.action.metadata;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.talend.dataprep.api.type.Type;

/**
 * Delete row when value is empty.
 */
@Component(DeleteEmpty.ACTION_BEAN_PREFIX + DeleteEmpty.DELETE_EMPTY_ACTION_NAME)
public class DeleteEmpty extends AbstractDelete {

    /** The action name. */
    public static final String DELETE_EMPTY_ACTION_NAME = "delete_empty"; //$NON-NLS-1$

    /**
     * Private constructor to ensure IoC.
     */
    private DeleteEmpty() {
    }

    /**
     * @see ActionMetadata#getName()
     */
    @Override
    public String getName() {
        return DELETE_EMPTY_ACTION_NAME;
    }

    /**
     * @see AbstractDelete#toDelete(Map, String)
     */
    @Override
    public boolean toDelete(Map<String, String> parsedParameters, String value) {
        return value == null || value.trim().length() == 0;
    }

    /**
     * @see ActionMetadata#getCompatibleColumnTypes()
     */
    @Override
    public Set<Type> getCompatibleColumnTypes() {
        return Collections.singleton(Type.ANY);
    }

}
