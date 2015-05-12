package org.talend.dataprep.transformation.api.transformer.json;

import com.fasterxml.jackson.core.JsonGenerator;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.transformation.api.transformer.TransformerWriter;
import org.talend.dataprep.transformation.exception.TransformationErrorCodes;

import java.io.IOException;
import java.util.List;

public class JsonWriter implements TransformerWriter {
    private final JsonGenerator generator;

    public JsonWriter(final JsonGenerator generator) {
        this.generator = generator;
    }

    @Override
    public void write(final List<ColumnMetadata> columns) throws IOException {
        startArray();
        columns.stream().forEach((col) -> {
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
