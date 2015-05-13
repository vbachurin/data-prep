package org.talend.dataprep.transformation.api.transformer.type;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.transformation.api.transformer.TransformerWriter;
import org.talend.dataprep.transformation.api.transformer.input.TransformerConfiguration;
import org.talend.dataprep.transformation.exception.TransformationErrorCodes;

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

    @Override
    public void process(final TransformerConfiguration configuration) {
        final TransformerWriter writer = configuration.getOutput();
        final JsonParser parser = configuration.getInput();
        try {
            JsonToken nextToken;

            writer.startObject();
            while ((nextToken = parser.nextToken()) != null) {
                if (nextToken == JsonToken.FIELD_NAME) {
                    switch (parser.getText()) {
                    case "columns":
                        writer.fieldName("columns");
                        columnsTransformer.process(configuration);
                        break;
                    case "records":
                        writer.fieldName("records");
                        recordsTransformer.process(configuration);
                        break;
                    }
                }
            }
            writer.endObject();
            writer.flush();

        } catch (IOException e) {
            throw new TDPException(TransformationErrorCodes.UNABLE_TO_PARSE_JSON, e);
        }
    }
}
