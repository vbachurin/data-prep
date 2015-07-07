package org.talend.dataprep.transformation.api.transformer;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

/**
 * Interface used by all TransformerFactories.
 */
@Component
public class TransformerFactory {

    @Autowired
    private Transformer[] transformers;

    // Intentionally left private to prevent non-Spring managed initialization.
    private TransformerFactory() {
    }

    /**
     * Generate the wanted Transformer.
     * 
     * @return the Transformer.
     * @param configuration A {@link TransformerConfiguration configuration} for a transformation.
     */
    public Transformer get(@Nonnull TransformerConfiguration configuration) {
        List<Transformer> electedTransformers = new LinkedList<>();
        for (Transformer transformer : transformers) {
            if (transformer.accept(configuration)) {
                electedTransformers.add(transformer);
            }
        }
        if (electedTransformers.isEmpty()) {
            throw new IllegalStateException("No transformers eligible for configuration.");
        }
        if (electedTransformers.size() > 1) {
            throw new IllegalStateException("Too many transformers eligible for configuration (got " + electedTransformers.size()
                    + ": " + Arrays.toString(electedTransformers.toArray()) + ")");
        }
        return electedTransformers.get(0);
    }

}
