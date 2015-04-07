package org.talend.dataprep.api.dataset.json;

import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;

@Component
public class ColumnMetadataModule extends SimpleModule {

    public ColumnMetadataModule() {
        super(ColumnMetadata.class.getName(), new Version(1, 0, 0, null, null, null));
        addDeserializer(ColumnMetadata.class, new ColumnMetadataJsonDeserializer());
        addSerializer(ColumnMetadata.class, new ColumnMetadataJsonSerializer());
    }

}
