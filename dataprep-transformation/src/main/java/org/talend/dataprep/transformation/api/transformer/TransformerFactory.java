package org.talend.dataprep.transformation.api.transformer;

/**
 * Interface used by all TransformerFactories.
 */
public interface TransformerFactory {

    /**
     * Generate the wanted Transformer.
     * 
     * @return the Transformer.
     */
    Transformer get();

}
