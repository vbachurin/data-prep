package org.talend.dataprep.dataset.store;

import java.io.InputStream;

public interface DataSetContentStore {

    void store(DataSet dataSet, InputStream dataSetContent);
}
