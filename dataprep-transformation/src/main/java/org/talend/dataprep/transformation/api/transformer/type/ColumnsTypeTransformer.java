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

        final List<Consumer<RowMetadata>> actions = configuration.getActions(RowMetadata.class);

        try {
            final StringWriter content = new StringWriter();
            final JsonGenerator contentGenerator = new JsonFactory().createGenerator(content);

            JsonToken nextToken;
            int level = 0;
            while ((nextToken = parser.nextToken()) != null) {
                switch (nextToken) {
                case VALUE_EMBEDDED_OBJECT:
                    contentGenerator.writeRaw(parser.getText());
                    break;
                case NOT_AVAILABLE:
                    break;
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
                    level++;
                    contentGenerator.writeStartArray();
                    break;
                case END_ARRAY:
                    contentGenerator.writeEndArray();
                    level--;
                    if (level == 0) {
                        contentGenerator.flush();

                        RowMetadata rowMetadata = getRowMetadata(content);

                        Consumer<RowMetadata> action = configuration.isPreview() ? actions.get(1) : actions.get(0);
                        action.accept(rowMetadata);

                        // setup the diff in case of preview
                        if (configuration.isPreview()) {
                            RowMetadata reference = getRowMetadata(content);
                            Consumer<RowMetadata> referenceAction = actions.get(0);
                            referenceAction.accept(reference);
                            rowMetadata.diff(reference);
                        }

                        // write the result
                        configuration.getOutput().write(rowMetadata);
                        return;

                    }
                case VALUE_NULL:
                default:
                    break;
                }
            }
        } catch (JsonParseException e) {
            throw new TDPException(TransformationErrorCodes.UNABLE_TO_PARSE_JSON, e);
        } catch (IOException e) {
            throw new TDPException(TransformationErrorCodes.UNABLE_TO_WRITE_JSON, e);
        }
    }

    /**
     * Return the row metadata from the given json string.
     * 
     * @param content - the String writer that contains JSON columns metadata.
     * @throws IOException if an error occurs.
     */
    private RowMetadata getRowMetadata(final StringWriter content) throws IOException {
        final ObjectReader columnReader = builder.build().reader(ColumnMetadata.class);
        List<ColumnMetadata> columns = columnReader.<ColumnMetadata> readValues(content.toString()).readAll();
        return new RowMetadata(columns);
    }
}
