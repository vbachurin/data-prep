package org.talend.dataprep.transformation.api.transformer.type;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.transformation.api.transformer.input.TransformerConfiguration;
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
     * @see TypeTransformer#process(TransformerConfiguration)
     */
    @Override
    public void process(final TransformerConfiguration configuration) {
        final JsonParser parser = configuration.getInput();

        try {
            final StringWriter content = new StringWriter();
            final JsonGenerator contentGenerator = new JsonFactory().createGenerator(content);

            JsonToken nextToken;
            while ((nextToken = parser.nextToken()) != null) {
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
                    contentGenerator.writeFieldName(parser.getText());
                    break;
                case VALUE_FALSE:
                    contentGenerator.writeBoolean(false);
                    break;
                case VALUE_TRUE:
                    contentGenerator.writeBoolean(true);
                    break;
                case VALUE_NUMBER_FLOAT:
                    contentGenerator.writeNumber(parser.getNumberValue().floatValue());
                    break;
                case VALUE_NUMBER_INT:
                    contentGenerator.writeNumber(parser.getNumberValue().intValue());
                    break;
                case VALUE_STRING:
                    contentGenerator.writeString(parser.getText());
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
                    columns = transform(columns, configuration);
                    configuration.getOutput().write(columns);

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
     * @param configuration transformation configuration.
     * @return the transformed columns.
     */
    private List<ColumnMetadata> transform(final List<ColumnMetadata> columns, TransformerConfiguration configuration) {

        RowMetadata rowMetadata = new RowMetadata(columns);

        List<Consumer<RowMetadata>> actions = configuration.getActions(RowMetadata.class);

        for (Consumer<RowMetadata> action : actions) {
            action.accept(rowMetadata);
        }
        return rowMetadata.getColumns();
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
