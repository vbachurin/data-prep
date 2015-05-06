package org.talend.dataprep.schema.io;

import java.io.*;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.springframework.stereotype.Service;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.exception.CommonErrorCodes;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.schema.CSVFormatGuess;
import org.talend.dataprep.schema.Serializer;

import au.com.bytecode.opencsv.CSVReader;

@Service("serializer#csv")
public class CSVSerializer implements Serializer {

    @Override
    public InputStream serialize(InputStream rawContent, DataSetMetadata metadata) {
        try {
            final Map<String, String> parameters = metadata.getContent().getParameters();
            final String separator = parameters.get(CSVFormatGuess.SEPARATOR_PARAMETER);
            CSVReader reader = new CSVReader(new InputStreamReader(rawContent), separator.charAt(0));
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
            return new ByteArrayInputStream(writer.toString().getBytes("UTF-8")); //$NON-NLS-1$
        } catch (IOException e) {
            throw new TDPException(CommonErrorCodes.UNABLE_TO_SERIALIZE_TO_JSON, e);
        }
    }
}
