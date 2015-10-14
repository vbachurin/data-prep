package org.talend.dataprep.transformation.api.transformer;

import org.talend.dataprep.api.dataset.DataSet;
import org.talend.dataprep.transformation.api.transformer.configuration.Configuration;

/**
 * Base interface used to transform (apply preparations to) dataset content.
 */
public interface Transformer {

    /**
     * Transform (apply preparations to) data content.
     * 
     * @param input the dataset content.
     * @param configuration Configuration of the transformation (format format...).
     */
    void transform(DataSet input, Configuration configuration);

    /**
     * @param configuration Configuration of a transformation (format format...).
     * @return <code>true</code> if {@link Transformer transformer} implementation handles <code>configuration</code>,
     * <code>false</code> otherwise.
     */
    boolean accept(Configuration configuration);

}
