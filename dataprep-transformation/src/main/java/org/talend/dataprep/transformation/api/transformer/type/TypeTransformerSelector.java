package org.talend.dataprep.transformation.api.transformer.type;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.transformation.api.transformer.TransformerWriter;
import org.talend.dataprep.transformation.api.transformer.input.TransformerConfiguration;
import org.talend.dataprep.transformation.exception.TransformationErrorCodes;

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
     * @see TypeTransformer#process(TransformerConfiguration)
     */
    @Override
    public void process(final TransformerConfiguration configuration) {
        try {
            final TransformerWriter writer = configuration.getOutput();
            writer.startObject();
            {
                writer.fieldName("columns");
                columnsTransformer.process(configuration);
                writer.fieldName("records");
                recordsTransformer.process(configuration);
            }
            writer.endObject();
            writer.flush();
        } catch (IOException e) {
            throw new TDPException(TransformationErrorCodes.UNABLE_TO_PARSE_JSON, e);
        }
    }
}
