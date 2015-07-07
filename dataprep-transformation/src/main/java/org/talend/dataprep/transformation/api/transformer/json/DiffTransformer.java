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
 * Transformer that preview the transformation (puts additional json content so that the front can display the
 * difference between current and previous transformation).
 */
@Component
class DiffTransformer implements Transformer {

    /** The data-prep ready jackson module. */
    @Autowired
    private Jackson2ObjectMapperBuilder builder;

    /** The transformer selector that routes transformation according the content to transform. */
    @Autowired
    private TransformerStepSelector transformerSelector;

    /**
     * Starts the transformation in preview mode.
     * @param input the dataset content.
     * @param configuration
     */
    @Override
    public void transform(DataSet input, TransformerConfiguration configuration) {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null.");
        }
        transformerSelector.process(configuration);
    }

    @Override
    public boolean accept(TransformerConfiguration configuration) {
        return configuration.isPreview();
    }
}
