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
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-YYYY HH:mm"); //$NON-NLS-1
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            return dateFormat.parse(jp.getText()).getTime();
        } catch (ParseException e) {
            throw new InvalidFormatException("Cannot convert date to EPOCH.", jp.getText(), Long.class);
        }
    }
}
