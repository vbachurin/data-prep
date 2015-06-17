package org.talend.dataprep.transformation.api.transformer.exporter.json;

import java.io.IOException;
import java.io.OutputStream;

import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.transformation.api.transformer.TransformerWriter;
import org.talend.dataprep.transformation.exception.TransformationErrorCodes;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

public class JsonWriter implements TransformerWriter {

    private final JsonGenerator generator;

    public JsonWriter(final JsonGenerator generator) {
        this.generator = generator;
    }

    public static JsonWriter create(Jackson2ObjectMapperBuilder builder, OutputStream output) {
        try {
            final JsonFactory factory = builder.build().getFactory();
            final JsonGenerator generator = factory.createGenerator(output);
            generator.setCodec(builder.build());
            return new JsonWriter(generator);
        } catch (IOException e) {
            throw new TDPException(TransformationErrorCodes.UNABLE_TO_PARSE_JSON, e);
        }
    }

    @Override
    public void write(final RowMetadata rowMetadata) throws IOException {
        startArray();
        rowMetadata.getColumns().stream().forEach(col -> {
            try {
                generator.writeObject(col);
            } catch (IOException e) {
                throw new TDPException(TransformationErrorCodes.UNABLE_TO_PARSE_JSON, e);
            }
        });
        endArray();
    }

    @Override
    public void write(final DataSetRow row) throws IOException {
        generator.writeObject(row.values());
    }

    @Override
    public void startArray() throws IOException {
        generator.writeStartArray();
    }

    @Override
    public void endArray() throws IOException {
        generator.writeEndArray();
    }

    @Override
    public void startObject() throws IOException {
        generator.writeStartObject();
    }

    @Override
    public void endObject() throws IOException {
        generator.writeEndObject();
    }

    @Override
    public void fieldName(String name) throws IOException {
        generator.writeFieldName(name);
    }

    @Override
    public void flush() throws IOException {
        generator.flush();
    }
}
