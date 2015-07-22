package org.talend.dataprep.api.dataset.json;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.RowMetadata;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class DataSetRowStreamDeserializer extends JsonDeserializer<Stream<DataSetRow>> {

    @Override
    public Stream<DataSetRow> deserialize(JsonParser jp, DeserializationContext context) throws IOException {
        final List<ColumnMetadata> columns = (List<ColumnMetadata>) context.getAttribute(ColumnContextDeserializer.class.getName());
        final RowMetadata rowMetadata;
        if (columns == null) {
            rowMetadata = new RowMetadata();
        } else {
            rowMetadata = new RowMetadata(columns);
        }
        final Iterable<DataSetRow> rowIterable = () -> new DataSetRowIterator(jp, rowMetadata);
        return StreamSupport.stream(rowIterable.spliterator(), false);
    }
}
