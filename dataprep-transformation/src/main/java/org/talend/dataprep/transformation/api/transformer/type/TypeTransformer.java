package org.talend.dataprep.transformation.api.transformer.type;

import org.talend.dataprep.transformation.api.transformer.input.TransformerConfiguration;

/**
 * Definition of a Dataset content Type (column, records, ...) transformer/serializer.
 */
public interface TypeTransformer {

    /**
     * Serialize and write the json parser content into the output stream
     *

     */
    void process(final TransformerConfiguration configuration);
}
