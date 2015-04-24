package org.talend.dataprep.transformation.api.transformer.type;

import java.io.IOException;
import java.util.function.Consumer;

import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.exception.Exceptions;
import org.talend.dataprep.transformation.exception.TransformationMessages;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

/**
 * Records array serializer
 */
@Component
public class RecordsTypeTransformer implements TypeTransformer<DataSetRow> {

    @Override
    public void process(final JsonParser parser, final JsonGenerator generator, final Consumer<DataSetRow> action) {
        try {
            final DataSetRow row = new DataSetRow();
            String currentFieldName = "";

            JsonToken nextToken;
            while ((nextToken = parser.nextToken()) != null) {
                switch (nextToken) {
                // Object delimiter
                case START_OBJECT:
                    row.clear();
                    break;
                case END_OBJECT:
                    action.accept(row);

                    if (!row.isDeleted()) {
                        row.writeTo(generator);
                    }

                    break;

                // DataSetRow fields
                case FIELD_NAME:
                    currentFieldName = parser.getText();
                    break;
                case VALUE_STRING:
                    row.set(currentFieldName, parser.getText());
                    break;

                // Array delimiter : on array end, we consider the column part ends
                case START_ARRAY:
                    generator.writeStartArray();
                    break;
                case END_ARRAY:
                    generator.writeEndArray();
                    generator.flush();
                    return;

                }
            }
        } catch (IOException e) {
            throw Exceptions.User(TransformationMessages.UNABLE_TO_PARSE_JSON, e);
        }
    }
}
