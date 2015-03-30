package org.talend.dataprep.api.preparation.json;

import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.api.preparation.Preparation;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class PreparationMetadataModule extends SimpleModule {

    public static final Module DEFAULT = new PreparationMetadataModule();

    private PreparationMetadataModule() {
        super(DataSetMetadata.class.getName(), new Version(1, 0, 0, null, null, null));
        addDeserializer(Preparation.class, new PreparationJsonDeserializer());
        addSerializer(Preparation.class, new PreparationJsonSerializer());
    }

}
