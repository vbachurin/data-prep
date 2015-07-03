package org.talend.dataprep.api.dataset.json;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class EpochTimeSerializer extends JsonSerializer<Long> {

    @Override
    public void serialize(Long value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeString(Instant.ofEpochMilli(value.longValue()).atZone(ZoneId.of("UTC"))
                .format(DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm")));
    }
}
