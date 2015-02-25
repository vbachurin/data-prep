package org.talend.dataprep.api.json;

import com.fasterxml.jackson.core.JsonToken;
import org.apache.commons.lang.NotImplementedException;
import org.talend.dataprep.api.DataSetMetadata;

class Selector implements State {

    public static final State INSTANCE = new Selector();

    private String currentField;

    @Override
    public void handle(Context context, DataSetMetadata.Builder builder, JsonToken token) throws Exception {
        if (token == null) {
            return;
        }
        switch (token) {
        case FIELD_NAME:
            currentField = context.getJsonParser().getCurrentName();
            break;
        case START_OBJECT:
            switch (currentField) {
            case "metadata":
                context.setCurrent(Metadata.INSTANCE);
                break;
            default:
                throw new NotImplementedException("No support for '" + currentField + "'.");
            }
        case START_ARRAY:
            switch (currentField) {
            case "columns":
                context.setCurrent(Columns.INSTANCE);
                break;
            default:
                throw new NotImplementedException();
            }
        default:
            break;
        }
    }
}
