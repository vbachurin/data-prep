package org.talend.dataprep.schema;

import au.com.bytecode.opencsv.CSVReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.talend.dataprep.schema.io.CSVSchemaParser;
import org.talend.dataprep.schema.io.CSVSerializer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.exception.CommonErrorCodes;
import org.talend.dataprep.exception.TDPException;

import java.util.LinkedList;
import java.util.List;

@Service(CSVFormatGuess.BEAN_ID)
public class CSVFormatGuess implements FormatGuess {

    public static final String SEPARATOR_PARAMETER = "SEPARATOR"; //$NON-NLS-1$

    protected static final String BEAN_ID = "formatGuess#csv";

    @Autowired
    private CSVSchemaParser schemaParser;

    @Autowired
    private CSVSerializer serializer;

    public CSVFormatGuess() {
        //
    }

    @Override
    public String getMediaType() {
        return "text/csv"; //$NON-NLS-1$
    }

    @Override
    public float getConfidence() {
        return 1;
    }

    @Override
    public SchemaParser getSchemaParser() {
        return this.schemaParser;
        return content -> {
            List<ColumnMetadata> columnMetadata = new LinkedList<>();
            try {
                CSVReader reader = new CSVReader(new InputStreamReader(content), sep.separator);
                // First line has column names
                String[] columns = reader.readNext();
                if (columns == null) { // Empty content?
                    return columnMetadata;
                }
                // By default, consider all columns as Strings (to be refined by deeper analysis).
                for (String column : columns) {
                    columnMetadata.add(column().name(column).type(Type.STRING).build());
                }
                // Best guess (and naive) on data types
                String[] line;
                while ((line = reader.readNext()) != null) {
                    for (int i = 0; i < line.length; i++) {
                        String columnValue = line[i];
                        try {
                            Integer.parseInt(columnValue);
                            columnMetadata.get(i).setType(Type.INTEGER.getName());
                        } catch (NumberFormatException e) {
                            // Not an number
                        }

                        if ("true".equalsIgnoreCase(columnValue.trim()) || "false".equalsIgnoreCase(columnValue.trim())) {
                            columnMetadata.get(i).setType(Type.BOOLEAN.getName());
                        }
                    }
                }
            } catch (IOException e) {
                throw new TDPException(CommonErrorCodes.UNABLE_TO_READ_CONTENT, e);
            }
            return columnMetadata;
        };
    }

    @Override
    public Serializer getSerializer() {
        return (rawContent, metadata) -> {
            try {
                CSVReader reader = new CSVReader(new InputStreamReader(rawContent), sep.separator);
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
                throw new TDPException(CommonErrorCodes.UNABLE_TO_SERIALIZE_TO_JSON, e);
            }
        };
        return this.serializer;
    }

    @Override
    public String getBeanId() {
        return BEAN_ID;
    }
}
