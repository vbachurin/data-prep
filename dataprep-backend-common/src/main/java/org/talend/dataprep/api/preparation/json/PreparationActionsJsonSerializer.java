package org.talend.dataprep.api.preparation.json;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.talend.dataprep.api.preparation.PreparationActions;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

@Component
public class PreparationActionsJsonSerializer extends JsonSerializer<PreparationActions> {

    @Override
    public void serialize(PreparationActions actions, JsonGenerator generator, SerializerProvider serializerProvider)
            throws IOException {
        generator.writeStartArray();
        generator.writeObject(actions.getActions());
        generator.writeEndArray();
    }
}
