package org.talend.dataprep.transformation.api.transformer.type;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.exception.CommonErrorCodes;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.transformation.api.transformer.TransformerWriter;
import org.talend.dataprep.transformation.api.transformer.TransformerConfiguration;

@Component
public class ColumnWriteStep implements TransformerStep {

    @Override
    public void process(TransformerConfiguration configuration) {

        // write the result
        try {
            final TransformerWriter writer = configuration.getOutput();
            writer.fieldName("columns");
            int index = configuration.isPreview() ? 1 : 0;
            final RowMetadata metadata = configuration.getTransformationContext(index).getTransformedRowMetadata();
            writer.write(metadata);
        } catch (IOException e) {
            throw new TDPException(CommonErrorCodes.UNABLE_TO_WRITE_JSON, e);
        }
    }
}
