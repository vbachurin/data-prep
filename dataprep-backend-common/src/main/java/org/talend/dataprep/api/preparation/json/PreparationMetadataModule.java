package org.talend.dataprep.api.preparation.json;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.preparation.Preparation;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;

@Component
public class PreparationMetadataModule extends SimpleModule {

    @Autowired
    PreparationJsonSerializer preparationJsonSerializer;


    public PreparationMetadataModule() {
        super(Preparation.class.getName(), new Version(1, 0, 0, null, null, null));
    }

    @PostConstruct
    public void init() {
        // We can omit deserialization as deserialization is straight forward.
        addSerializer(Preparation.class, preparationJsonSerializer);
    }

}
