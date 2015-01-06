package org.talend.dataprep.dataset.store.local;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.talend.dataprep.dataset.objects.DataSetMetadata;
import org.talend.dataprep.dataset.store.DataSetContentStore;

import java.io.*;

public class LocalDataSetContentStore implements DataSetContentStore {

    private static final Log LOGGER = LogFactory.getLog(LocalDataSetContentStore.class);

    private static File getFile(DataSetMetadata dataSetMetadata) {
        return new File(System.getProperty("java.io.tmpdir") + "/talend/tdp/datasets/" + dataSetMetadata.getId()); //$NON-NLS-1  //$NON-NLS-2 
    }

    @Override
    public void store(DataSetMetadata dataSetMetadata, InputStream dataSetContent) {
        try {
            File dataSetFile = getFile(dataSetMetadata);
            org.apache.commons.io.FileUtils.touch(dataSetFile);
            FileOutputStream fos = new FileOutputStream(dataSetFile);
            IOUtils.copy(dataSetContent, fos);
            LOGGER.info("Data set #" + dataSetMetadata.getId() + " stored to '" + dataSetFile + "'.");
        } catch (IOException e) {
            throw new RuntimeException("Unable to save data set in temporary directory.", e);
        }
    }

    @Override
    public InputStream get(DataSetMetadata dataSetMetadata) {
        try {
            return new FileInputStream(getFile(dataSetMetadata));
        } catch (FileNotFoundException e) {
            LOGGER.warn("File '" + getFile(dataSetMetadata) + "' does not exist.");
            return new ByteArrayInputStream(new byte[0]);
        }
    }

    @Override
    public InputStream getAsRaw(DataSetMetadata dataSetMetadata) {
        try {
            return new FileInputStream(getFile(dataSetMetadata));
        } catch (FileNotFoundException e) {
            LOGGER.warn("File '" + getFile(dataSetMetadata) + "' does not exist.");
            return new ByteArrayInputStream(new byte[0]);
        }
    }

    @Override
    public void delete(DataSetMetadata dataSetMetadata) {
        if (!getFile(dataSetMetadata).delete()) {
            throw new RuntimeException("Unable to delete data set content #" + dataSetMetadata.getId());
        }
    }
}
