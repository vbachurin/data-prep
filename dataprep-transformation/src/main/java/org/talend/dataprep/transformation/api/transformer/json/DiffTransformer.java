package org.talend.dataprep.transformation.api.transformer.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.transformation.api.action.ParsedActions;
import org.talend.dataprep.transformation.api.transformer.Transformer;
import org.talend.dataprep.transformation.api.transformer.input.TransformerConfiguration;
import org.talend.dataprep.transformation.api.transformer.type.TypeTransformerSelector;
import org.talend.dataprep.transformation.exception.TransformationErrorCodes;

/**
 * Transformer that preview the transformation (puts additional json content so that the front can display the
 * difference between current and previous transformation).
 */
@Component
@Scope("request")
class DiffTransformer implements Transformer {

    /** The data-prep ready jackson module. */
    @Autowired
    private Jackson2ObjectMapperBuilder builder;

    /** The transformer selector that routes transformation according the content to transform. */
    @Autowired
    private TypeTransformerSelector tranformerSelector;

    /** The previous action. */
    private final ParsedActions previousAction;

    /** The new action to preview. */
    private final ParsedActions newAction;

    /** The row indexes to show preview (because the preview is not performed on the whole dataset). */
    private final List<Integer> indexes;

    /**
     * Constructor.
     *
     * @param indexes the indexes of the row to preview the change.
     * @param previousAction the previous action.
     * @param newAction the new action to preview.
     */
    DiffTransformer(final List<Integer> indexes, final ParsedActions previousAction, final ParsedActions newAction) {
        this.previousAction = previousAction;
        this.newAction = newAction;
        this.indexes = indexes == null ? null : new ArrayList<>(indexes);
    }

    /**
     * Starts the transformation in preview mode.
     *
     * @param input the dataset content.
     * @param output where to output the transformation.
     */
    @Override
    public void transform(InputStream input, OutputStream output) {
        try {
            if (input == null) {
                throw new IllegalArgumentException("Input cannot be null.");
            }
            if (output == null) {
                throw new IllegalArgumentException("Output cannot be null.");
            }

            //@formatter:off
            final TransformerConfiguration configuration = getDefaultConfiguration(input, output, builder)
                    .indexes(indexes)
                    .preview(true)
                    .actions(DataSetRow.class, previousAction.getRowTransformer())
                    .actions(DataSetRow.class, newAction.getRowTransformer())
                    .actions(RowMetadata.class, previousAction.getMetadataTransformer())
                    .actions(RowMetadata.class, newAction.getMetadataTransformer())
                    .build();
            //@formatter:on

            tranformerSelector.process(configuration);

            output.flush();

        } catch (IOException e) {
            throw new TDPException(TransformationErrorCodes.UNABLE_TO_PARSE_JSON, e);
        }
    }
}
