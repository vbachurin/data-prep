package org.talend.dataprep.api.dataset.json;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

public class EpochTimeDeserializer extends JsonDeserializer<Long> {

    @Override
    public Long deserialize(JsonParser jp, DeserializationContext context) throws IOException {
        try {
            SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM-dd-YYYY HH:mm"); //$NON-NLS-1
            DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
            return DATE_FORMAT.parse(jp.getText()).getTime();
        } catch (ParseException e) {
            throw new InvalidFormatException("Cannot convert date to EPOCH.", jp.getText(), Long.class);
        }
    }
}
