package org.talend.dataprep.transformation.api.transformer;

public interface TransformerFactory {

    /**
     * Generate the wanted Transformer
     * @return the Transformer
     */
    Transformer get();

    /**
     * The actions to perform in transformation
     * @param actions
     */
    TransformerFactory withActions(final String... actions);

    /**
     * The records indexes to transform. The transformer does not set the index to deleted row after transformation
     * @param indexes
     */
    TransformerFactory withIndexes(final String indexes);
}
