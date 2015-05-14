package org.talend.dataprep.api.dataset.json;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.context.ApplicationContext;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.api.dataset.Quality;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class DataSetMetadataJsonSerializer extends JsonSerializer<DataSetMetadata> {

    private final SimpleDataSetMetadataJsonSerializer metadataJsonSerializer;

    private final boolean metadata;

    private final boolean columns;

    private final InputStream stream;

    public DataSetMetadataJsonSerializer(boolean metadata, boolean columns, InputStream stream,
                                         ApplicationContext applicationContext) {
        this.metadata = metadata;
        this.columns = columns;
        this.stream = stream;
        this.metadataJsonSerializer = new SimpleDataSetMetadataJsonSerializer(applicationContext);
    }

    @Override
    public void serialize(DataSetMetadata dataSetMetadata, JsonGenerator generator, SerializerProvider serializerProvider)
        throws IOException {
        generator.writeStartObject();
        {
            // Write general information about the dataset
            if (metadata) {
                generator.writeFieldName("metadata"); //$NON-NLS-1
                metadataJsonSerializer.serialize(dataSetMetadata, generator);
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
                            // Writes statistics as given by statistics library.
                            generator.writeFieldName("statistics"); //$NON-NLS-1$
                            generator.writeRawValue(column.getStatistics());
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