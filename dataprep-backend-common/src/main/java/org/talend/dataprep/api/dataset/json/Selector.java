package org.talend.dataprep.api.dataset.json;

import org.apache.commons.lang.NotImplementedException;
import org.talend.dataprep.api.dataset.DataSetMetadata;

import com.fasterxml.jackson.core.JsonToken;

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
            startObject(context);
            break;
        case START_ARRAY:
            startArray(context);
            break;
        default:
            break;
        }
    }

    private void startArray(Context context) {
        switch (currentField) {
        case "columns": //$NON-NLS-1$
            context.setCurrent(Columns.INSTANCE);
            break;
        default:
            throw new NotImplementedException();
        }
    }

    private void startObject(Context context) {
        switch (currentField) { //$NON-NLS-1$
        case "metadata":
            context.setCurrent(Metadata.INSTANCE);
            break;
        default:
            throw new NotImplementedException("No support for '" + currentField + "'.");
        }
    }
}
