package org.talend.dataprep.dataset.store.local;

import java.io.*;
import java.util.Base64;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.talend.dataprep.api.DataSetContent;
import org.talend.dataprep.api.DataSetMetadata;
import org.talend.dataprep.dataset.service.analysis.schema.Serializer;
import org.talend.dataprep.dataset.store.DataSetContentStore;

public class LocalDataSetContentStore implements DataSetContentStore {

    private static final Log LOGGER = LogFactory.getLog(LocalDataSetContentStore.class);

    private final String storeLocation;

    public LocalDataSetContentStore(String storeLocation) {
        if (storeLocation == null) {
            throw new IllegalArgumentException("Store location cannot be null.");
        }
        if (!storeLocation.endsWith("/")) { //$NON-NLS-1$
            storeLocation += "/"; //$NON-NLS-1$
        }
        LOGGER.info("Content store location: " + storeLocation);
        this.storeLocation = storeLocation;
    }

    private File getFile(DataSetMetadata dataSetMetadata) {
        return new File(storeLocation + dataSetMetadata.getId());
    }

    @Override
    public void store(DataSetMetadata dataSetMetadata, InputStream dataSetJsonContent, String actions) {
        try {
            LOGGER.info("Actions: " + new String(Base64.getDecoder().decode(actions)));
            LOGGER.info("Content: " + IOUtils.toString(dataSetJsonContent));
        } catch (IOException e) {
            LOGGER.error("Unable to dump content & actions.", e);
        }
    }

    @Override
    public void storeAsRaw(DataSetMetadata dataSetMetadata, InputStream dataSetContent) {
        try {
            File dataSetFile = getFile(dataSetMetadata);
            FileUtils.touch(dataSetFile);
            FileOutputStream fos = new FileOutputStream(dataSetFile);
            IOUtils.copy(dataSetContent, fos);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Data set #" + dataSetMetadata.getId() + " stored to '" + dataSetFile + "'.");
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to save data set in temporary directory.", e);
        }
    }

    @Override
    public InputStream get(DataSetMetadata dataSetMetadata) {
        DataSetContent content = dataSetMetadata.getContent();
        Serializer serializer = content.getContentType().getSerializer();
        return serializer.serialize(getAsRaw(dataSetMetadata), dataSetMetadata);
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
        if (getFile(dataSetMetadata).exists()) {
            if (!getFile(dataSetMetadata).delete()) {
                throw new RuntimeException("Unable to delete data set content #" + dataSetMetadata.getId());
            }
        } else {
            LOGGER.warn("Data set #" + dataSetMetadata.getId() + " has no content.");
        }
    }

    @Override
    public void clear() {
        try {
            FileUtils.deleteDirectory(new File(storeLocation));
        } catch (IOException e) {
            throw new RuntimeException("Unable to clear content store.", e);
        }
    }
}
