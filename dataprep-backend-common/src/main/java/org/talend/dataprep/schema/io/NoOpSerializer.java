package org.talend.dataprep.schema.io;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.springframework.stereotype.Service;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.schema.Serializer;

@Service("serializer#any")
public class NoOpSerializer implements Serializer {

    @Override
    public InputStream serialize(InputStream rawContent, DataSetMetadata metadata) {
        return new ByteArrayInputStream(new byte[0]);
    }
}
