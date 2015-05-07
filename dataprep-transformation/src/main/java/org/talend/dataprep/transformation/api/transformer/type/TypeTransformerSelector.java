package org.talend.dataprep.transformation.api.transformer.type;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.transformation.exception.TransformationErrorCodes;

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
    public void process(final JsonParser parser, final JsonGenerator generator, final List<Integer> indexes, boolean preview, final Consumer<DataSetRow>... actions) {

        try {
            JsonToken nextToken;

            generator.writeStartObject();
            while ((nextToken = parser.nextToken()) != null) {
                if (nextToken == JsonToken.FIELD_NAME) {
                    switch (parser.getText()) {
                    case "columns":
                        generator.writeFieldName("columns");
                        columnsTransformer.process(parser, generator, null, preview);
                        break;
                    case "records":
                        generator.writeFieldName("records");
                        recordsTransformer.process(parser, generator, indexes, preview, actions);
                        break;
                    }
                }
            }
            generator.writeEndObject();
            generator.flush();
        } catch (IOException e) {
            throw new TDPException(TransformationErrorCodes.UNABLE_TO_PARSE_JSON, e);
        }
    }
}
