package org.talend.dataprep.transformation.api.transformer;

import java.io.IOException;

import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.RowMetadata;

public interface TransformerWriter {

    void write(RowMetadata columns) throws IOException;

    void write(DataSetRow row) throws IOException;

    default void startArray() throws IOException {
    }

    default void endArray() throws IOException {
    }

    default void startObject() throws IOException {
    }

    default void endObject() throws IOException {
    }

    default void fieldName(String columns) throws IOException {
    }

    default void flush() throws IOException {
    }
}
