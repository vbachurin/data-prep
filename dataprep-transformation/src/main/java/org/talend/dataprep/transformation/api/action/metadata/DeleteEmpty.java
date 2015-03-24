package org.talend.dataprep.transformation.api.action.metadata;

import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DeleteEmpty extends AbstractDelete {

    public static final String DELETE_EMPTY_ACTION_NAME = "delete_empty"; //$NON-NLS-1$

    public static final ActionMetadata INSTANCE = new DeleteEmpty();

    @Bean(name = ACTION_BEAN_PREFIX + DELETE_EMPTY_ACTION_NAME)
    public ActionMetadata createInstance() {
        return new DeleteEmpty();
    }

    // Please do not instanciate this class, it is spring Bean automatically instanciated.
    public DeleteEmpty() {
    }

    @Override
    public String getName() {
        return DELETE_EMPTY_ACTION_NAME;
    }

    @Override
    public boolean toDelete(Map<String, String> parsedParameters, String value) {
        return (value == null || value.trim().length() == 0);
    }

}
