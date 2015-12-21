package org.talend.dataprep.date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

/**
 * LocalDateTime deserializer. It deserialize properly default jackson serialized LocalDateTime.
 */
public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
    @Override
    public LocalDateTime deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        final JsonNode node = parser.getCodec().readTree(parser);
        final int year = (Integer) node.get("year").numberValue();
        final String month = node.get("month").asText();
        final int day = (Integer) node.get("dayOfMonth").numberValue();
        final int hour = (Integer) node.get("hour").numberValue();
        final int minute = (Integer) node.get("minute").numberValue();
        final int second = (Integer) node.get("second").numberValue();

        return LocalDateTime.of(year, Month.valueOf(month), day, hour, minute, second);
    }
}
