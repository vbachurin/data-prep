package org.talend.dataprep.api.dataset.json;

import java.io.IOException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.talend.dataprep.api.dataset.DataSetRow;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class DataSetRowStreamDeserializer extends JsonDeserializer<Stream<DataSetRow>> {

    @Override
    public Stream<DataSetRow> deserialize(JsonParser jp, DeserializationContext context) throws IOException {
        final Iterable<DataSetRow> rowIterable = () -> new DataSetRowIterator(jp);
        return StreamSupport.stream(rowIterable.spliterator(), false);
    }
}
