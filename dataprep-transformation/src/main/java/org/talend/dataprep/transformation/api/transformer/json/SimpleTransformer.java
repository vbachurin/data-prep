package org.talend.dataprep.transformation.api.transformer.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
 * Base implementation of the Transformer interface.
 */
@Component
@Scope("request")
class SimpleTransformer implements Transformer {

    /** The data-prep jackson builder. */
    @Autowired
    private Jackson2ObjectMapperBuilder builder;

    /** The transformer selector that selects which transformer to use depending on the input content. */
    @Autowired
    private TypeTransformerSelector typeStateSelector;

    /** The parsed actions ready to be applied to a dataset. */
    private final ParsedActions actions;

    /**
     * Default Constructor.
     * 
     * @param actions the actions to perform.
     */
    SimpleTransformer(ParsedActions actions) {
        this.actions = actions;
    }

    /**
     * @see Transformer#transform(InputStream, OutputStream)
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
                    .preview(false)
                    .actions(DataSetRow.class, actions.getRowTransformer())
                    .actions(RowMetadata.class, actions.getMetadataTransformer())
                    .build();
            //@formatter:on

            typeStateSelector.process(configuration);
            output.flush();
        } catch (IOException e) {
            throw new TDPException(TransformationErrorCodes.UNABLE_TO_PARSE_JSON, e);
        }
    }
}
