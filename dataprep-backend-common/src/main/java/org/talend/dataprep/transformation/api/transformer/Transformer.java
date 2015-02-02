package org.talend.dataprep.transformation.api.transformer;

import java.io.InputStream;
import java.io.OutputStream;

public interface Transformer {

    void transform(InputStream input, OutputStream output);
}
