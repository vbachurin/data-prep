package org.talend.dataprep.api.dataset.json;

import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.DataSetMetadata;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;

@Component
public class DataSetMetadataModule extends SimpleModule {


    @Autowired
    private ApplicationContext applicationContext;

    public static SimpleModule get(boolean metadata, boolean columns, InputStream records, ApplicationContext applicationContext) {
        return new DataSetMetadataModule(metadata, columns, records, applicationContext);
    }

    public DataSetMetadataModule() {
        super(DataSetMetadata.class.getName(), new Version(1, 0, 0, null, null, null));
        addDeserializer(DataSetMetadata.class, new DataSetMetadataJsonDeserializer());
        addSerializer(DataSetMetadata.class, new DataSetMetadataJsonSerializer(true, true, null,applicationContext));
    }

    private DataSetMetadataModule(boolean metadata, boolean columns, InputStream records, ApplicationContext applicationContext) {
        super(DataSetMetadata.class.getName(), new Version(1, 0, 0, null, null, null));
        addDeserializer(DataSetMetadata.class, new DataSetMetadataJsonDeserializer());
        addSerializer(DataSetMetadata.class, new DataSetMetadataJsonSerializer(metadata, columns, records, applicationContext));
    }

}
