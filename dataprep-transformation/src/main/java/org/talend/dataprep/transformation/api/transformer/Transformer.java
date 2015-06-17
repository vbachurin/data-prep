package org.talend.dataprep.transformation.api.transformer;

import java.io.IOException;
import java.io.OutputStream;

import org.talend.dataprep.api.dataset.DataSet;
import org.talend.dataprep.transformation.api.transformer.input.TransformerConfiguration;

/**
 * Base interface used to transform (apply preparations to) dataset content.
 */
public interface Transformer {

    /**
     * Transform (apply preparations to) data content.
     *  @param input the dataset content.
     * @param output where to output the transformation.
     */
    void transform(DataSet input, OutputStream output);

    default TransformerConfiguration.Builder from(DataSet input) throws IOException {
        return TransformerConfiguration.builder().input(input);
    }
}
