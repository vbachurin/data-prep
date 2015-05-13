package org.talend.dataprep.transformation.api.transformer.type;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.transformation.api.transformer.TransformerWriter;
import org.talend.dataprep.transformation.api.transformer.input.TransformerConfiguration;
import org.talend.dataprep.transformation.exception.TransformationErrorCodes;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

/**
 * TypeTransformer selector. This class create the transformation content structure and delegate the value
 * transformation/serialization to other specific TypeTransformers.
 */
@Component
public class TypeTransformerSelector implements TypeTransformer {

    @Autowired
    private ColumnsTypeTransformer columnsTransformer;

    @Autowired
    private RecordsTypeTransformer recordsTransformer;

    @Override
    public void process(final TransformerConfiguration configuration) {
        final TransformerWriter writer = configuration.getWriter();
        final JsonParser parser = configuration.getParser();
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
