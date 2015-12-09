package org.talend.dataprep.date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;

/**
 * LocalDate deserializer. It deserialize properly default jackson serialized LocalDate.
 */
public class LocalDateDeserializer extends JsonDeserializer<LocalDate> {
    @Override
    public LocalDate deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        final JsonNode node = parser.getCodec().readTree(parser);
        final int year = (Integer) node.get("year").numberValue();
        final String month = node.get("month").asText();
        final int day = (Integer) node.get("dayOfMonth").numberValue();

        return LocalDate.of(year, Month.valueOf(month), day);
    }
}
