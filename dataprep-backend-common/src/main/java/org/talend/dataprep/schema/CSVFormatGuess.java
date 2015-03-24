package org.talend.dataprep.schema;

import static org.talend.dataprep.api.dataset.ColumnMetadata.Builder.column;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.ColumnMetadata;
import org.talend.dataprep.api.type.Type;

import au.com.bytecode.opencsv.CSVReader;

class CSVFormatGuess implements FormatGuess {

    private final Separator sep;

    public CSVFormatGuess(Separator sep) {
        this.sep = sep;
    }

    @Override
    public String getMediaType() {
        return "text/csv";
    }

    @Override
    public float getConfidence() {
        return 1;
    }

    @Override
    public SchemaParser getSchemaParser() {
        return content -> {
            List<ColumnMetadata> columnMetadata = new LinkedList<>();
            try {
                CSVReader reader = new CSVReader(new InputStreamReader(content), sep.separator);
                // First line has column names
                String[] columns = reader.readNext();
                if (columns == null) { // Empty content?
                    return columnMetadata;
                }
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

                        if (columnValue.trim().equalsIgnoreCase("true") || columnValue.trim().equalsIgnoreCase("false")) {
                            columnMetadata.get(i).setType(Type.BOOLEAN.getName());
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Unable to read content", e);
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
                throw new RuntimeException("Unable to serialize to JSON.", e);
            }
        };
    }
}
