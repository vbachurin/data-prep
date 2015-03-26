package org.talend.dataprep.preparation.store;

import java.io.InputStream;

public interface ContentCache {

    boolean has(String preparationId, String stepId);

    InputStream get(String preparationId, String stepId);
}
