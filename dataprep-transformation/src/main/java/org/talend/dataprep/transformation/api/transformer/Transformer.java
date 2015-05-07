package org.talend.dataprep.transformation.api.transformer;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public interface Transformer {
    void transform(InputStream input, OutputStream output);
}
