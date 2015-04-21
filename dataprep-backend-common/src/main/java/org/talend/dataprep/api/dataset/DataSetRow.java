package org.talend.dataprep.api.dataset;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.talend.dataprep.exception.CommonMessages;
import org.talend.dataprep.exception.Exceptions;

public class DataSetRow {

    private final JsonFactory jsonFactory = new JsonFactory();

    private boolean deleted = false;

    private final Map<String, String> values = new HashMap<>();

    public DataSetRow() {
    }

    public DataSetRow(Map<String, String> values) {
        this.values.putAll(values);
    }

    /**
     * Set an entry in the dataset row
     * @param name - the key
     * @param value - the value
     */
    public DataSetRow set(final String name, final String value) {
        values.put(name, value);
        return this;
    }

    /**
     * Get the value associated with the provided key
     * @param name - the key
     * @return - the value as string
     */
    public String get(final String name) {
        return values.get(name);
    }

    /**
     * Check if the row is deleted
     */
    public boolean isDeleted() {
        return this.deleted;
    }

    /**
     * Set whether the row is deleted
     */
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    /**
     * Write the row as JSON in the provided OutputStream
     * @param stream - the stream to write to
     */
    public void writeTo(final OutputStream stream) {
        if (isDeleted()) {
            return;
        }

        try {
            final JsonGenerator jGenerator = jsonFactory.createJsonGenerator(stream);
            jGenerator.writeStartObject();

            values.entrySet().stream().forEach((entry) -> {
                try {
                    jGenerator.writeStringField(entry.getKey(), entry.getValue());
                } catch (IOException e) {
                    throw Exceptions.User(CommonMessages.UNABLE_TO_SERIALIZE_TO_JSON, e);
                }
            });

            jGenerator.writeEndObject();
            jGenerator.flush();

        } catch (IOException e) {
            throw Exceptions.User(CommonMessages.UNABLE_TO_SERIALIZE_TO_JSON, e);
        }
    }

    /**
     * Clear all values in this row and reset state as it was when created (e.g. {@link #isDeleted()} returns
     * <code>false</code>).
     */
    public void clear() {
        deleted = false;
        values.clear();
    }
}
