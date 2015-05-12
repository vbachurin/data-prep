package org.talend.dataprep.transformation.api.transformer;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Base interface used to transform (apply preparations to) dataset content.
 */
public interface Transformer {

    /**
     * Transform (apply preparations to) data content.
     * 
     * @param input the dataset content.
     * @param output where to output the transformation.
     */
    void transform(InputStream input, OutputStream output);
}
