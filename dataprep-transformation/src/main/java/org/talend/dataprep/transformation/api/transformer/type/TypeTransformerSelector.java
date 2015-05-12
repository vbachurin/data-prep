package org.talend.dataprep.transformation.api.transformer.type;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.transformation.api.action.ParsedActions;
import org.talend.dataprep.transformation.exception.TransformationErrorCodes;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

/**
 * Delegate to the correct transformer depending on the input content : {@link ColumnsTypeTransformer} for the columns
 * header. {@link RecordsTypeTransformer} for the records header.
 */
@Component
public class TypeTransformerSelector implements TypeTransformer {

    /** The columns transformer that transforms RowMetadata. */
    @Autowired
    private ColumnsTypeTransformer columnsTransformer;

    /** The records transformer that works on dataset rows. */
    @Autowired
    private RecordsTypeTransformer recordsTransformer;

    /**
     * @see TypeTransformer#process(JsonParser, JsonGenerator, List, boolean, ParsedActions...)
     */
    public void process(final JsonParser input, final JsonGenerator output, final List<Integer> indexes, boolean preview,
            final ParsedActions... actions) {

        try {
            JsonToken nextToken;

            output.writeStartObject();
            while ((nextToken = input.nextToken()) != null) {
                if (nextToken == JsonToken.FIELD_NAME) {
                    switch (input.getText()) {
                    case "columns":
                        output.writeFieldName("columns");
                        columnsTransformer.process(input, output, null, preview, actions);
                        break;
                    case "records":
                        output.writeFieldName("records");
                        recordsTransformer.process(input, output, indexes, preview, actions);
                        break;
                    }
                }
            }
            output.writeEndObject();
            output.flush();
        } catch (IOException e) {
            throw new TDPException(TransformationErrorCodes.UNABLE_TO_PARSE_JSON, e);
        }
    }
}
