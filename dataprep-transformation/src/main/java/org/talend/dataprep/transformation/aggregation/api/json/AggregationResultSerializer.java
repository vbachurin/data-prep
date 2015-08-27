package org.talend.dataprep.transformation.aggregation.api.json;

import java.io.IOException;
import java.util.Iterator;

import org.talend.dataprep.transformation.aggregation.api.AggregationResult;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Serialize Aggregation in json.
 */
public class AggregationResultSerializer extends JsonSerializer<AggregationResult> {

    /**
     * @see JsonSerializer#serialize(Object, JsonGenerator, SerializerProvider)
     */
    @Override
    public void serialize(AggregationResult value, JsonGenerator gen, SerializerProvider provider) throws IOException {

        gen.writeStartArray();
        final Iterator<String> keys = value.keys().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            gen.writeStartObject();
            gen.writeStringField("data", key);
            gen.writeNumberField(value.getOperator().name(), value.get(key).getValue());
            gen.writeEndObject();
        }
        gen.writeEndArray();
    }
}
