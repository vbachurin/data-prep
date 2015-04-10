package org.talend.dataprep.schema;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetMetadata;

@Component("serializer#xls")
public class XlsSerializer implements Serializer {

    @Override
    public InputStream serialize(InputStream rawContent, DataSetMetadata metadata) {

        /*
        try {


            StringWriter writer = new StringWriter();
            JsonGenerator generator = new JsonFactory().createJsonGenerator(writer);
            reader.readNext(); // Skip column names
            String[] line;
            generator.writeStartArray();
            {
                while ((line = reader.readNext()) != null) {
                    List<ColumnMetadata> columns = metadata.getRow().getColumns();
                    generator.writeStartObject();
                    for (int i = 0; i < columns.size(); i++) {
                        ColumnMetadata columnMetadata = columns.get(i);
                        generator.writeStringField(columnMetadata.getId(), line[i]);
                    }
                    generator.writeEndObject();
                }
            }
            generator.writeEndArray();
            generator.flush();
            return new ByteArrayInputStream(writer.toString().getBytes("UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException("Unable to serialize to JSON.", e);
        }
        */

        return IOUtils.toInputStream("");
    }
}
