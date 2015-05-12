package org.talend.dataprep.transformation.api.transformer.type;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.transformation.api.action.ParsedActions;
import org.talend.dataprep.transformation.exception.TransformationErrorCodes;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.ObjectReader;

/**
 * Transformer that works on RowMetadata.
 */
@Component
public class ColumnsTypeTransformer implements TypeTransformer {

    /** The data-prep ready json module. */
    @Autowired
    private Jackson2ObjectMapperBuilder builder;

    /**
     * @see TypeTransformer#process(JsonParser, JsonGenerator, List, boolean, ParsedActions...)
     */
    @Override
    public void process(final JsonParser input, final JsonGenerator output, final List<Integer> indexes, final boolean preview,
            final ParsedActions... actions) {
        try {
            // TODO should the content be written directly to the output instead of a buffer as in RecordTypeTransformer
            // ?
            final StringWriter content = new StringWriter();
            final JsonGenerator contentGenerator = new JsonFactory().createGenerator(content);

            JsonToken nextToken;
            while ((nextToken = input.nextToken()) != null) {
                // TODO nextToken == JsonToken.VALUE_EMBEDDED_OBJECT
                switch (nextToken) {

                // Object delimiter
                case START_OBJECT:
                    contentGenerator.writeStartObject();
                    break;
                case END_OBJECT:
                    contentGenerator.writeEndObject();
                    break;

                // Fields key/value
                case FIELD_NAME:
                    contentGenerator.writeFieldName(input.getText());
                    break;
                case VALUE_FALSE:
                    contentGenerator.writeBoolean(false);
                    break;
                case VALUE_TRUE:
                    contentGenerator.writeBoolean(true);
                    break;
                case VALUE_NUMBER_FLOAT:
                    contentGenerator.writeNumber(input.getNumberValue().floatValue());
                    break;
                case VALUE_NUMBER_INT:
                    contentGenerator.writeNumber(input.getNumberValue().intValue());
                    break;
                case VALUE_STRING:
                    contentGenerator.writeString(input.getText());
                    break;

                // Array delimiter : on array end, we consider the column part ends
                case START_ARRAY:
                    contentGenerator.writeStartArray();
                    break;
                case END_ARRAY:
                    contentGenerator.writeEndArray();
                    contentGenerator.flush();

                    // get the original columns
                    List<ColumnMetadata> columns = getColumnsMetadata(content);

                    // transform them and write them
                    columns = transform(columns, preview, actions);
                    write(output, columns);

                    return;
                }
            }

        } catch (JsonParseException e) {
            throw new TDPException(TransformationErrorCodes.UNABLE_TO_PARSE_JSON, e);
        } catch (IOException e) {
            throw new TDPException(TransformationErrorCodes.UNABLE_TO_WRITE_JSON, e);
        }
    }

    /**
     * Apply columns transformations.
     *
     * @param columns the columns list to transform.
     * @param preview true if the additional preview details should be written
     * @param actions the actions to perform.
     * @return the transformed columns.
     */
    private List<ColumnMetadata> transform(final List<ColumnMetadata> columns, boolean preview, final ParsedActions... actions) {
        List<ColumnMetadata> result = columns;
        for (ParsedActions action : actions) {
            List<Function<List<ColumnMetadata>, List<ColumnMetadata>>> functions = action.getMetadataTransformers();
            for (Function<List<ColumnMetadata>, List<ColumnMetadata>> function : functions) {
                result = function.apply(result);
            }
        }
        return result;
    }

    /**
     * Convert String to list of ColumnMetadataObject
     * 
     * @param content - the String writer that contains JSON format array
     * @throws IOException
     */
    private List<ColumnMetadata> getColumnsMetadata(final StringWriter content) throws IOException {
        final ObjectReader columnReader = builder.build().reader(ColumnMetadata.class);
        return columnReader.<ColumnMetadata> readValues(content.toString()).readAll();
    }

}
