package org.talend.dataprep.api.preparation.json;

import java.io.IOException;

import org.talend.dataprep.api.preparation.Preparation;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

class PreparationJsonDeserializer extends JsonDeserializer<Preparation> {

    @Override
    public Preparation deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        return null;
    }

}
