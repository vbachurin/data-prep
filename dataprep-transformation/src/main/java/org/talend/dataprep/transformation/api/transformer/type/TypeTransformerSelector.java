package org.talend.dataprep.transformation.api.transformer.type;

import java.io.IOException;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.exception.Exceptions;
import org.talend.dataprep.transformation.exception.TransformationMessages;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

/**
 * TypeTransformer selector. This class create the transformation content structure and delegate the value
 * transformation/serialization to other specific TypeTransformers.
 */
@Component
public class TypeTransformerSelector implements TypeTransformer<DataSetRow> {

    @Autowired
    private ColumnsTypeTransformer columnsTransformer;

    @Autowired
    private RecordsTypeTransformer recordsTransformer;

    @Override
    public void process(final JsonParser parser, final JsonGenerator generator, final Consumer<DataSetRow> action) {

        try {
            JsonToken nextToken;

            generator.writeStartObject();
            while ((nextToken = parser.nextToken()) != null) {
                if (nextToken == JsonToken.FIELD_NAME) {
                    switch (parser.getText()) {
                    case "columns":
                        generator.writeFieldName("columns");
                        columnsTransformer.process(parser, generator, null);
                        break;
                    case "records":
                        generator.writeFieldName("records");
                        recordsTransformer.process(parser, generator, action);
                        break;
                    }
                }
            }
            generator.writeEndObject();
            generator.flush();
        } catch (IOException e) {
            throw Exceptions.User(TransformationMessages.UNABLE_TO_PARSE_JSON, e);
        }
    }
}
