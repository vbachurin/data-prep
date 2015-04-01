package org.talend.dataprep.transformation.api.action.metadata;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.talend.dataprep.api.type.Type;

@Component(DeleteEmpty.ACTION_BEAN_PREFIX + DeleteEmpty.DELETE_EMPTY_ACTION_NAME)
public class DeleteEmpty extends AbstractDelete {

    public static final String DELETE_EMPTY_ACTION_NAME = "delete_empty"; //$NON-NLS-1$

    private DeleteEmpty() {
    }

    @Override
    public String getName() {
        return DELETE_EMPTY_ACTION_NAME;
    }

    @Override
    public boolean toDelete(Map<String, String> parsedParameters, String value) {
        return (value == null || value.trim().length() == 0);
    }

    @Override
    public Set<Type> getCompatibleColumnTypes() {
        return Collections.singleton(Type.STRING);
    }

}
