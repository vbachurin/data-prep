package org.talend.dataprep.api.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.talend.dataprep.api.ColumnMetadata;
import org.talend.dataprep.api.DataSetMetadata;
import org.talend.dataprep.api.Quality;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

class DataSetMetadataJsonSerializer extends JsonSerializer<DataSetMetadata> {

    static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM-dd-YYYY HH:mm"); //$NON-NLS-1
    static
    {
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    private final boolean metadata;

    private final boolean columns;

    private final InputStream stream;

    public DataSetMetadataJsonSerializer(boolean metadata, boolean columns, InputStream stream) {
        this.metadata = metadata;
        this.columns = columns;
        this.stream = stream;
    }

    @Override
    public void serialize(DataSetMetadata dataSetMetadata, JsonGenerator generator, SerializerProvider serializerProvider) throws IOException {
        generator.writeStartObject();
        {
            // Write general information about the dataset
            if (metadata) {
                generator.writeFieldName("metadata"); //$NON-NLS-1
                generator.writeStartObject();
                {
                    generator.writeStringField("id", dataSetMetadata.getId()); //$NON-NLS-1
                    generator.writeStringField("name", dataSetMetadata.getName()); //$NON-NLS-1
                    generator.writeStringField("author", dataSetMetadata.getAuthor() ); //$NON-NLS-1
                    generator.writeNumberField("records", dataSetMetadata.getContent().getNbRecords()); //$NON-NLS-1
                    generator.writeNumberField("nbLinesHeader", dataSetMetadata.getContent().getNbLinesInHeader()); //$NON-NLS-1
                    generator.writeNumberField("nbLinesFooter", dataSetMetadata.getContent().getNbLinesInFooter() ); //$NON-NLS-1
                    synchronized (DATE_FORMAT) {
                        generator.writeStringField("created", DATE_FORMAT.format(dataSetMetadata.getCreationDate())); //$NON-NLS-1
                    }
                }
                generator.writeEndObject();
            }
            // Write columns
            if (columns) {
                generator.writeFieldName("columns"); //$NON-NLS-1
                generator.writeStartArray();
                for (ColumnMetadata column : dataSetMetadata.getRow().getColumns()) {
                    generator.writeStartObject();
                    {
                        // Column name
                        generator.writeStringField("id", column.getId()); //$NON-NLS-1
                        // Column quality
                        if (dataSetMetadata.getLifecycle().qualityAnalyzed()) {
                            generator.writeFieldName("quality"); //$NON-NLS-1
                            Quality quality = column.getQuality();
                            generator.writeStartObject();
                            {
                                generator.writeNumberField("empty", quality.getEmpty()); //$NON-NLS-1
                                generator.writeNumberField("invalid", quality.getInvalid()); //$NON-NLS-1
                                generator.writeNumberField("valid", quality.getValid()); //$NON-NLS-1
                            }
                            generator.writeEndObject();
                        }
                        // Column type
                        String typeName = dataSetMetadata.getLifecycle().schemaAnalyzed() ? column.getType() : "N/A"; //$NON-NLS-1
                        generator.writeStringField("type", typeName); //$NON-NLS-1
                    }
                    generator.writeEndObject();
                }
                generator.writeEndArray();
            }
            // Records
            if (stream != null) {
                generator.writeFieldName("records");
                generator.flush(); // <- Important! Flush before dumping records!
                generator.writeRaw(':');
                // Put here content as provided by stream
                byte[] buffer = new byte[1024];
                int read;
                while ((read = stream.read(buffer)) > 0) {
                    for (int i = 0; i < read; i++) {
                        generator.writeRaw((char) buffer[i]);
                    }
                }
            }
        }

        generator.writeEndObject();
        generator.flush();

    }
}
