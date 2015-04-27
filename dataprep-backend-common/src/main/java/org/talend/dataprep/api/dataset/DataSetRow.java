package org.talend.dataprep.api.dataset;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.talend.dataprep.exception.CommonMessages;
import org.talend.dataprep.exception.Exceptions;

import com.fasterxml.jackson.core.JsonGenerator;

public class DataSetRow {
    private final static String DIFF_KEY = "__tdpDiff";
    private final static String DELETED_KEY = "__tdpDeleted";
    private final static String DIFF_NEW = "new";
    private final static String DIFF_UPDATE = "update";

    private boolean deleted = false;

    private final Map<String, String> values = new HashMap<>();

    public DataSetRow() {
    }

    public DataSetRow(Map<String, String> values) {
        this.values.putAll(values);
    }

    public Map<String, String> cloneValues() {
        return new HashMap<>(values);
    }

    /**
     * Set an entry in the dataset row
     * 
     * @param name - the key
     * @param value - the value
     */
    public DataSetRow set(final String name, final String value) {
        values.put(name, value);
        return this;
    }

    /**
     * Get the value associated with the provided key
     * 
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
     * 
     * @param jGenerator - the json generator plugged to stream to write to
     */
    public void writeTo(final JsonGenerator jGenerator) {
        if (isDeleted()) {
            return;
        }

        try {
            jGenerator.writeStartObject();
            writeValues(jGenerator, values);
            jGenerator.writeEndObject();
            jGenerator.flush();

        } catch (IOException e) {
            throw Exceptions.User(CommonMessages.UNABLE_TO_SERIALIZE_TO_JSON, e);
        }
    }

    /**
     * Write the row preview as JSON in the provided OutputStream
     *
     * @param jGenerator - the json generator plugged to stream to write to
     * @param originalValues - unchanged values for preview diff
     */
    public void writePreviewTo(final JsonGenerator jGenerator, final Map<String, String> originalValues) {
        final Map<String, String> diff = new HashMap<>();

        try {
            jGenerator.writeStartObject();

            //row has been deleted : write the old values with DELETE_KEY property
            if(isDeleted()) {
                jGenerator.writeBooleanField(DELETED_KEY, true);
                writeValues(jGenerator, originalValues);
            }

            //row has been updated : write the new values and get the diff for each value, then write the DIFF_KEY property
            else {
                values.entrySet().stream().forEach((entry) -> {
                    try {
                        final String originalValue = originalValues.get(entry.getKey());
                        if(originalValue == null) {
                            diff.put(entry.getKey(), DIFF_NEW);
                        }
                        else if(! entry.getValue().equals(originalValue)) {
                            diff.put(entry.getKey(), DIFF_UPDATE);
                        }

                        jGenerator.writeStringField(entry.getKey(), entry.getValue());
                    } catch (IOException e) {
                        throw Exceptions.User(CommonMessages.UNABLE_TO_SERIALIZE_TO_JSON, e);
                    }
                });

                jGenerator.writeObjectFieldStart(DIFF_KEY);
                writeValues(jGenerator, diff);
                jGenerator.writeEndObject();
            }

            jGenerator.writeEndObject();
            jGenerator.flush();

        } catch (IOException e) {
            throw Exceptions.User(CommonMessages.UNABLE_TO_SERIALIZE_TO_JSON, e);
        }
    }

    private void writeValues(final JsonGenerator jGenerator, final Map<String, String> valuesToWrite) {
        valuesToWrite.entrySet().stream().forEach((entry) -> {
            try {
                jGenerator.writeStringField(entry.getKey(), entry.getValue());
            } catch (IOException e) {
                throw Exceptions.User(CommonMessages.UNABLE_TO_SERIALIZE_TO_JSON, e);
            }
        });
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
