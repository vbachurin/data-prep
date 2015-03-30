package org.talend.dataprep.transformation.api.transformer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Consumer;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.exception.Exceptions;
import org.talend.dataprep.transformation.exception.Messages;

class SimpleTransformer implements Transformer {

    private final Consumer<DataSetRow> action;

    SimpleTransformer(Consumer<DataSetRow> action) {
        this.action = action;
    }

    @Override
    public void transform(InputStream input, OutputStream output) {
        try {
            if (input == null) {
                throw new IllegalArgumentException("Input cannot be null.");
            }
            if (output == null) {
                throw new IllegalArgumentException("Output cannot be null.");
            }
            JsonFactory factory = new JsonFactory();
            JsonParser parser = factory.createJsonParser(input);
            boolean metRecords = false;
            String currentFieldName = null;
            JsonToken nextToken;
            // Move to the "records" element in stream
            while (!metRecords) {
                nextToken = parser.nextToken();
                if (nextToken == JsonToken.FIELD_NAME) {
                    if ("records".equals(parser.getText())) { //$NON-NLS-1$
                        metRecords = true;
                    }
                } else if (nextToken == null) { // EOF
                    return;
                }
            }
            // Parsing array of records in "records" field
            if (parser.nextToken() != JsonToken.START_ARRAY) {
                throw new IllegalArgumentException("Incorrect stream (expected an array).");
            }
            nextToken = parser.nextToken();
            output.write("{\"records\":[".getBytes()); //$NON-NLS-1$
            boolean firstRow = true;
            DataSetRow row = new DataSetRow();
            while (nextToken != JsonToken.END_ARRAY) {
                if (nextToken == JsonToken.START_OBJECT) {
                    if (!firstRow) {
                        output.write(',');
                    } else {
                        firstRow = false;
                    }
                } else if (nextToken == JsonToken.FIELD_NAME) {
                    currentFieldName = parser.getText(); // Column name
                } else if (nextToken == JsonToken.VALUE_STRING) {
                    row.set(currentFieldName, parser.getText()); // Value
                } else if (nextToken == JsonToken.END_OBJECT) {
                    action.accept(row);
                    if (row.isDeleted()) {
                        firstRow = true;
                    } else {
                        row.writeTo(output);
                    }
                    row.clear(); // Clear values (allow to safely reuse DataSetRow instance)
                }
                nextToken = parser.nextToken();
            }
            output.write("]}".getBytes()); //$NON-NLS-1$
            output.flush();
        } catch (IOException e) {
            throw Exceptions.User(Messages.UNABLE_TO_PARSE_JSON, e);
        }
    }
}
