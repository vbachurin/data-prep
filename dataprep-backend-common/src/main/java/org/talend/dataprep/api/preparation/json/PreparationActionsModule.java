package org.talend.dataprep.api.preparation.json;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.preparation.PreparationActions;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;

@Component
public class PreparationActionsModule extends SimpleModule {

    @Autowired
    PreparationActionsJsonSerializer preparationActionsJsonSerializer;

    public PreparationActionsModule() {
        super(PreparationActions.class.getName(), new Version(1, 0, 0, null, null, null));
    }

    @PostConstruct
    public void init() {
        // We can omit deserialization as deserialization is straight forward.
        addSerializer(PreparationActions.class, preparationActionsJsonSerializer);
    }

}
