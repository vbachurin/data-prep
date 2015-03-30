package org.talend.dataprep.api.dataset.json;

import org.talend.dataprep.api.dataset.DataSetMetadata;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

class Context {

    private final DataSetMetadata.Builder builder;

    private final JsonParser jsonParser;

    private State current = new Metadata();

    public Context(DataSetMetadata.Builder builder, JsonParser jsonParser) {
        this.builder = builder;
        this.jsonParser = jsonParser;
    }

    void handle(JsonToken token) {
        try {
            current.handle(this, builder, token);
        } catch (Exception e) {
            throw new RuntimeException("Unable to parse.", e);
        }
    }

    public JsonParser getJsonParser() {
        return jsonParser;
    }

    public void setCurrent(State current) {
        this.current = current;
    }

    public DataSetMetadata.Builder getBuilder() {
        return builder;
    }
}
