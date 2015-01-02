package org.talend.dataprep.dataset.store;


import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

class LocalDataSetContentStore implements DataSetContentStore {
    @Override
    public void store(DataSet dataSet, InputStream dataSetContent) {
        try {
            File dataSetFile = new File(System.getProperty("java.io.tmpdir") + "/talend/tdp/datasets/" + dataSet.getId());
            org.apache.commons.io.FileUtils.touch(dataSetFile);
            FileOutputStream fos = new FileOutputStream(dataSetFile);
            IOUtils.copy(dataSetContent, fos);
            System.out.println("dataSetFile = " + dataSetFile);
        } catch (IOException e) {
            throw new RuntimeException("Unable to save data set in temporary directory.", e);
        }
    }
}
