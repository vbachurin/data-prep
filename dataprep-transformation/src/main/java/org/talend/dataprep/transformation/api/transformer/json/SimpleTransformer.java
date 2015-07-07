package org.talend.dataprep.transformation.api.transformer.json;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.DataSet;
import org.talend.dataprep.transformation.api.transformer.Transformer;
import org.talend.dataprep.transformation.api.transformer.TransformerConfiguration;
import org.talend.dataprep.transformation.api.transformer.type.TransformerStepSelector;

/**
 * Base implementation of the Transformer interface.
 */
@Component
class SimpleTransformer implements Transformer {

    /** The data-prep jackson builder. */
    @Autowired
    private Jackson2ObjectMapperBuilder builder;

    /** The transformer selector that selects which transformer to use depending on the input content. */
    @Autowired
    private TransformerStepSelector typeStateSelector;

    /**
     * @see Transformer#transform(DataSet, TransformerConfiguration)
     */
    @Override
    public void transform(DataSet input, TransformerConfiguration configuration) {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null.");
        }
        typeStateSelector.process(configuration);
    }

    @Override
    public boolean accept(TransformerConfiguration configuration) {
        return !configuration.isPreview();
    }
}
