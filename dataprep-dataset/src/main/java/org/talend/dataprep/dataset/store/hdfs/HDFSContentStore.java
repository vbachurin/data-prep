package org.talend.dataprep.dataset.store.hdfs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.talend.dataprep.api.DataSetContent;
import org.talend.dataprep.api.DataSetMetadata;
import org.talend.dataprep.dataset.service.analysis.schema.Serializer;
import org.talend.dataprep.dataset.store.DataSetContentStore;

public class HDFSContentStore implements DataSetContentStore {

    private static final Log LOGGER = LogFactory.getLog(HDFSContentStore.class);

    private static final String HDFS_DIRECTORY = "talend/tdp/datasets/"; //$NON-NLS-1$

    private final FileSystem fileSystem;

    public HDFSContentStore(String hdfsStoreLocation) {
        try {
            fileSystem = FileSystem.get(new URI(hdfsStoreLocation), new Configuration());
            LOGGER.info("HDFS file system: " + fileSystem.getClass() + " (" + fileSystem.getUri() + ").");
        } catch (Exception e) {
            throw new RuntimeException("Unable to connect to '" + hdfsStoreLocation + "'.", e);
        }
    }

    private static Path getPath(DataSetMetadata dataSetMetadata) {
        return new Path(HDFS_DIRECTORY + dataSetMetadata.getId());
    }

    @Override
    public void store(DataSetMetadata dataSetMetadata, InputStream dataSetJsonContent, String actions) {

    }

    @Override
    public void storeAsRaw(DataSetMetadata dataSetMetadata, InputStream dataSetContent) {
        try (FSDataOutputStream outputStream = fileSystem.create(getPath(dataSetMetadata))) {
            IOUtils.copy(dataSetContent, outputStream);
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException("Unable to store content of data set #" + dataSetMetadata.getId(), e);
        }
    }

    @Override
    public InputStream get(DataSetMetadata dataSetMetadata) {
        DataSetContent content = dataSetMetadata.getContent();
        if (content.getContentType() != null) {
            Serializer serializer = content.getContentType().getSerializer();
            return serializer.serialize(getAsRaw(dataSetMetadata), dataSetMetadata);
        } else {
            return new ByteArrayInputStream(new byte[0]);
        }
    }

    @Override
    public InputStream getAsRaw(DataSetMetadata dataSetMetadata) {
        try {
            return fileSystem.open(getPath(dataSetMetadata));
        } catch (Exception e) {
            LOGGER.warn("File '" + getPath(dataSetMetadata) + "' does not exist.");
            return new ByteArrayInputStream(new byte[0]);
        }
    }

    @Override
    public void delete(DataSetMetadata dataSetMetadata) {
        try {
            fileSystem.delete(getPath(dataSetMetadata), true);
        } catch (IOException e) {
            throw new RuntimeException("Unable to delete content of data set #" + dataSetMetadata.getId(), e);
        }
    }

    @Override
    public void clear() {
        try {
            fileSystem.delete(new Path(HDFS_DIRECTORY), true);
        } catch (IOException e) {
            throw new RuntimeException("Unable to clear all data set content.", e);
        }
    }
}
