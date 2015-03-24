package org.talend.dataprep.api.dataset.json;

import java.util.*;

import org.apache.commons.lang.NotImplementedException;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetMetadata;

import com.fasterxml.jackson.core.JsonToken;

class Columns implements State {

    public static final State INSTANCE = new Columns();

    private final List<ColumnMetadata.Builder> columns = new LinkedList<>();

    private final Map<String, Object> values = new HashMap<>();

    private final EnumMap<Quality, Integer> quality = new EnumMap<>(Quality.class);

    private String currentField;

    private boolean parsingQuality;

    @Override
    public void handle(Context context, DataSetMetadata.Builder builder, JsonToken token) throws Exception {
        if (token == null) {
            return;
        }
        switch (token) {
        case NOT_AVAILABLE:
        case START_ARRAY:
            throw new IllegalStateException();
        case START_OBJECT:
            if (!parsingQuality) {
                values.clear();
                parsingQuality = false;
            }
            break;
        case END_ARRAY:
            builder.row(columns.toArray(new ColumnMetadata.Builder[columns.size()]));
            columns.clear();
            values.clear();
            context.setCurrent(Selector.INSTANCE);
            break;
        case END_OBJECT:
            if (parsingQuality) {
                parsingQuality = false;
            } else {
                ColumnMetadata.Builder columnBuilder = ColumnMetadata.Builder.column();
                columnBuilder.name((String) values.get("id"));
                if (!quality.isEmpty()) {
                    columnBuilder.empty(quality.get(Quality.EMPTY));
                    columnBuilder.invalid(quality.get(Quality.INVALID));
                    columnBuilder.valid(quality.get(Quality.VALID));
                    context.getBuilder().qualityAnalyzed(true);
                }
                String type = (String) values.get("type");
                if (type != null && !"N/A".equals(type)) {
                    context.getBuilder().schemaAnalyzed(true);
                    columnBuilder.type(Type.get(type));
                    columns.add(columnBuilder);
                }
            }
            break;
        case FIELD_NAME:
            currentField = context.getJsonParser().getCurrentName();
            if ("quality".equals(currentField)) {
                parsingQuality = true;
            }
            break;
        case VALUE_EMBEDDED_OBJECT:
            break;
        case VALUE_STRING:
            values.put(currentField, context.getJsonParser().getValueAsString());
            break;
        case VALUE_NUMBER_INT:
            int valueAsInt = context.getJsonParser().getValueAsInt();
            if (parsingQuality) {
                switch (currentField) {
                case "empty":
                    quality.put(Quality.EMPTY, valueAsInt);
                    break;
                case "invalid":
                    quality.put(Quality.INVALID, valueAsInt);
                    break;
                case "valid":
                    quality.put(Quality.VALID, valueAsInt);
                    break;
                default:
                    throw new NotImplementedException("No support for '" + currentField + "' for quality parsing.");
                }
            } else {
                values.put(currentField, valueAsInt);
            }
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

    enum Quality {
        EMPTY,
        INVALID,
        VALID
    }
}
