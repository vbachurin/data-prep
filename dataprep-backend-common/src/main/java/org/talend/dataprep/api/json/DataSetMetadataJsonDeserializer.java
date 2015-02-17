package org.talend.dataprep.api.json;

import java.io.IOException;

import org.talend.dataprep.api.DataSetMetadata;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

class DataSetMetadataJsonDeserializer extends JsonDeserializer<DataSetMetadata> {

    @Override
    public DataSetMetadata deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        DataSetMetadata.Builder builder = DataSetMetadata.Builder.metadata();
        Context context = new Context(builder, jsonParser);
        while (jsonParser.hasCurrentToken()) {
            JsonToken token = jsonParser.nextToken();
            context.handle(token);
        }
        return builder.build();
    }

}
