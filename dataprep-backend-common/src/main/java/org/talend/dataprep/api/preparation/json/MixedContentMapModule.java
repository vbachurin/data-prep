package org.talend.dataprep.api.preparation.json;

import java.io.IOException;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.elasticsearch.common.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.preparation.MixedContentMap;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * Jackson module that deals with MixedContentMap.
 * 
 * @see MixedContentMap
 */
@Component
public class MixedContentMapModule extends SimpleModule {

    /** DataPrep ready jackson builder. */
    @Autowired
    @Lazy // lazy to prevent circular dependency
    private Jackson2ObjectMapperBuilder builder;

    /**
     * Default empty constructor.
     */
    public MixedContentMapModule() {
        super(MixedContentMapModule.class.getName(), new Version(1, 0, 0, null, null, null));
    }

    /**
     * Set the Serializer / Deserializer.
     */
    @PostConstruct
    public void init() {
        addSerializer(MixedContentMap.class, new Serializer(builder));
        addDeserializer(MixedContentMap.class, new Deserializer());
    }

    /**
     * Serialize MixedContentMap to json.
     */
    private class Serializer extends JsonSerializer<MixedContentMap> {

        /** DataPrep ready jackson builder. */
        private Jackson2ObjectMapperBuilder builder;

        /**
         * Default constructor.
         * 
         * @param builder the dataprep ready jackson builder.
         */
        public Serializer(Jackson2ObjectMapperBuilder builder) {
            this.builder = builder;
        }

        /**
         * @see JsonSerializer#serialize(Object, JsonGenerator, SerializerProvider)
         */
        @Override
        public void serialize(MixedContentMap map, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                jsonGenerator.writeFieldName(entry.getKey());
                final String value = entry.getValue();
                if (value == null) {
                    jsonGenerator.writeNull();
                } else if (value.isEmpty()) {
                    jsonGenerator.writeString(StringUtils.EMPTY);
                } else if (value.charAt(0) == '{' || value.charAt(0) == '[') {
                    // check that it's a real json array or object
                    try {
                        builder.build().reader().readTree(value);
                        jsonGenerator.writeRaw(':' + value);
                    }
                    // otherwise, it is written as a string (may be a regular expression, e.g. [A-Za-z0-9]*)
                    catch (IOException ioe) {
                        jsonGenerator.writeString(value);
                    }
                } else {
                    jsonGenerator.writeString(value);
                }
            }
            jsonGenerator.writeEndObject();
        }
    }

    /**
     * Deserialize MixedContentMap to MixedContentMap.
     */
    private class Deserializer extends JsonDeserializer<MixedContentMap> {

        /**
         * @see JsonDeserializer#deserialize(JsonParser, DeserializationContext)
         */
        @Override
        public MixedContentMap deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
                throws IOException {
            final MixedContentMap map = new MixedContentMap();
            JsonToken token;
            String currentKey = null;
            while ((token = jsonParser.nextToken()) != null && token != JsonToken.END_OBJECT) {
                if (currentKey != null) {
                    if (token == JsonToken.VALUE_STRING) {
                        // Value is a string value, get value from underlying parser
                        map.put(currentKey, jsonParser.getValueAsString());
                    } else if (token == JsonToken.VALUE_NUMBER_INT) {
                        map.put(currentKey, String.valueOf(jsonParser.getValueAsInt()));
                    } else if (token == JsonToken.VALUE_NUMBER_FLOAT) {
                        map.put(currentKey, String.valueOf(jsonParser.getValueAsDouble()));
                    } else if (token == JsonToken.VALUE_TRUE || token == JsonToken.VALUE_FALSE) {
                        map.put(currentKey, String.valueOf(jsonParser.getValueAsBoolean()));
                    } else if (token == JsonToken.START_OBJECT || token == JsonToken.START_ARRAY) {
                        // Value is a JSON object, get content as is
                        map.put(currentKey, jsonParser.readValueAsTree().toString());
                        jsonParser.skipChildren();
                    } else if (token == JsonToken.VALUE_NULL) {
                        map.put(currentKey, null);
                    }
                    currentKey = null;
                } else if (token == JsonToken.FIELD_NAME) {
                    // New entry, keep it for put(...) calls
                    currentKey = jsonParser.getCurrentName();
                }
            }
            return map;
        }
    }
}
