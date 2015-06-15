package org.talend.dataprep.transformation.api.transformer.type;

import java.io.IOException;
import java.util.List;
import java.util.function.BiConsumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.DataSet;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.exception.CommonErrorCodes;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;
import org.talend.dataprep.transformation.api.transformer.input.TransformerConfiguration;

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
        final DataSet dataSet = configuration.getInput();
        final List<BiConsumer<RowMetadata, TransformationContext>> actions = configuration.getColumnActions();
        RowMetadata reference = new RowMetadata(dataSet.getColumns());
        RowMetadata rowMetadata = new RowMetadata(dataSet.getColumns());
        int index = configuration.isPreview() ? 1 : 0;
        TransformationContext context = configuration.getTransformationContext(index);
        BiConsumer<RowMetadata, TransformationContext> action = actions.get(index);
        action.accept(rowMetadata, context);
        // setup the diff in case of preview
        if (configuration.isPreview()) {
            BiConsumer<RowMetadata, TransformationContext> referenceAction = actions.get(0);
            TransformationContext referenceContext = configuration.getTransformationContext(0);
            referenceAction.accept(reference, referenceContext);
            rowMetadata.diff(reference);
            referenceContext.setTransformedRowMetadata(reference);
        }
        // write the result
        try {
            configuration.getOutput().write(rowMetadata);
        } catch (IOException e) {
            throw new TDPException(CommonErrorCodes.UNABLE_TO_WRITE_JSON, e);
        }
        // store the row metadata in the configuration for RecordsTypeTransformer use
        context.setTransformedRowMetadata(rowMetadata);
    }

}
