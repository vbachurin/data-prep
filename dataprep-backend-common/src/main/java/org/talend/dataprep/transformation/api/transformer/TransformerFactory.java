package org.talend.dataprep.transformation.api.transformer;

public interface TransformerFactory {

    Transformer get(String actions);

}
