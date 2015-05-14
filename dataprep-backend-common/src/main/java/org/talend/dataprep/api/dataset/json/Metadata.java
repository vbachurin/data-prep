package org.talend.dataprep.api.dataset.json;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.talend.dataprep.api.dataset.DataSetMetadata;

import com.fasterxml.jackson.core.JsonToken;

class Metadata implements State {

    public static final State INSTANCE = new Metadata();

    private final Map<String, Object> values = new HashMap<>();

    private String currentField;

    @Override
    public void handle(Context context, JsonToken token) throws Exception {
        if (token == null) {
            return;
        }
        final DataSetMetadata.Builder builder = context.getBuilder();
        switch (token) {
        case NOT_AVAILABLE:
        case START_ARRAY:
        case END_ARRAY:
            //throw new IllegalStateException("Unexpected '" + token + "'.");
        case START_OBJECT:
            break;
        case END_OBJECT:
            builder.id((String) values.get("id"));
            builder.name( (String) values.get( "name" ) );
            builder.author( (String) values.get( "author" ) );
            builder.sheetName( (String) values.get("sheetName") );
            String created = (String) values.get("created");
            if (created != null) {
                synchronized (SimpleDataSetMetadataJsonSerializer.DATE_FORMAT) {
                    Date date = SimpleDataSetMetadataJsonSerializer.DATE_FORMAT.parse(created);
                    builder.created(date.getTime());
                }
            }
            if (values.get("records") != null) {
                builder.size((Integer) values.get("records"));
                builder.headerSize((Integer) values.get("nbLinesHeader"));
                builder.footerSize((Integer) values.get("nbLinesFooter"));
                builder.contentAnalyzed(true);
            }
            values.clear();
            context.setCurrent(Selector.INSTANCE);
            break;
        case FIELD_NAME:
            currentField = context.getJsonParser().getCurrentName();
            break;
        case VALUE_EMBEDDED_OBJECT:
            break;
        case VALUE_STRING:
            values.put(currentField, context.getJsonParser().getValueAsString());
            break;
        case VALUE_NUMBER_INT:
            values.put(currentField, context.getJsonParser().getValueAsInt());
            break;
        case VALUE_NUMBER_FLOAT:
            values.put(currentField, context.getJsonParser().getValueAsLong());
            break;
        case VALUE_TRUE:
        case VALUE_FALSE:
            values.put(currentField, context.getJsonParser().getValueAsString());
            break;
        case VALUE_NULL:
            values.put(currentField, null);
            break;
        }
    }
}
