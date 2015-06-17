package org.talend.dataprep.transformation.api.transformer.type;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.transformation.api.transformer.TransformerWriter;
import org.talend.dataprep.transformation.api.transformer.input.TransformerConfiguration;
import org.talend.dataprep.transformation.exception.TransformationErrorCodes;

/**
 * Delegate to the correct transformer depending on the input content : {@link ColumnsTransformerStep} for the columns
 * header. {@link RecordsTransformerStep} for the records header.
 */
@Component
public class TransformerStepSelector implements TransformerStep {

    /** The columns transformer that transforms RowMetadata. */
    @Autowired
    private ColumnsTransformerStep columnsTransformer;

    @Autowired
    private ColumnWriteStep columnWriteStep;

    /** The records transformer that works on dataset rows. */
    @Autowired
    private RecordsTransformerStep recordsTransformer;

    /**
     * @see TransformerStep#process(TransformerConfiguration)
     */
    @Override
    public void process(final TransformerConfiguration configuration) {
        try {
            final TransformerWriter writer = configuration.getOutput();
            writer.startObject();
            {
                columnsTransformer.process(configuration);
                if (writer.requireMetadataForHeader()) {
                    columnWriteStep.process(configuration);
                    recordsTransformer.process(configuration);
                } else {
                    recordsTransformer.process(configuration);
                    columnWriteStep.process(configuration);
                }
            }
            writer.endObject();
            writer.flush();
        } catch (IOException e) {
            throw new TDPException(TransformationErrorCodes.UNABLE_TO_PARSE_JSON, e);
        }
    }
}
