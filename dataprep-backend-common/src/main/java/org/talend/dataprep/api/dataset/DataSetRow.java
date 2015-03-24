package org.talend.dataprep.api.dataset;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;

public class DataSetRow {

    private boolean                   deleted = false;

    private final Map<String, String> values;

    public DataSetRow(Map<String, String> values) {
        this.values = values;
    }

    public DataSetRow set(String name, String value) {
        values.put(name, value);
        return this;
    }

    public String get(String name) {
        return values.get(name);
    }

    public boolean isDeleted() {
        return this.deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public void writeTo(OutputStream stream) {
        if (isDeleted()) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        Iterator<Map.Entry<String, String>> iterator = values.entrySet().iterator();
        builder.append('{');
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            builder.append('\"').append(entry.getKey()).append('\"').append(':').append('\"').append(entry.getValue())
                    .append('\"');
            if (iterator.hasNext()) {
                builder.append(',');
            }
        }
        builder.append('}');
        try {
            stream.write(builder.toString().getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Unable to write row to stream.", e);
        }
    }
}
