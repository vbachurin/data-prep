package org.talend.dataprep.api.dataset.json;

import org.talend.dataprep.api.dataset.ColumnMetadata;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class ColumnMetadataModule extends SimpleModule {

    public static final Module DEFAULT = new ColumnMetadataModule();

    private ColumnMetadataModule() {
        super(ColumnMetadata.class.getName(), new Version(1, 0, 0, null, null, null));
        addDeserializer(ColumnMetadata.class, new ColumnMetadataJsonDeserializer());
        addSerializer(ColumnMetadata.class, new ColumnMetadataJsonSerializer());
    }

}
