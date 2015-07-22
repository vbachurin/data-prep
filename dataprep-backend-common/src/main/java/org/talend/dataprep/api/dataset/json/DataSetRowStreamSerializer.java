package org.talend.dataprep.api.dataset.json;

import java.io.IOException;
import java.util.stream.Stream;

import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.exception.CommonErrorCodes;
import org.talend.dataprep.exception.TDPException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class DataSetRowStreamSerializer extends JsonSerializer<Stream<DataSetRow>> {

    @Override
    public void serialize(Stream<DataSetRow> value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeStartArray();
        value.forEach(row -> {
            try {
                generator.writeObject(row.valuesWithId());
            } catch (IOException e) {
                throw new TDPException(CommonErrorCodes.UNABLE_TO_WRITE_JSON, e);
            }
        });
        generator.writeEndArray();
    }
}