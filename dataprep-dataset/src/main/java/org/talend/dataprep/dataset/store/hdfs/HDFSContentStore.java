package org.talend.dataprep.dataset.store.hdfs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataprep.api.dataset.DataSetContent;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.dataset.exception.DataSetErrorCodes;
import org.talend.dataprep.dataset.store.DataSetContentStore;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.schema.Serializer;

public class HDFSContentStore implements DataSetContentStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(HDFSContentStore.class);

    private static final String HDFS_DIRECTORY = "talend/tdp/datasets/"; //$NON-NLS-1$

    private final FileSystem fileSystem;

    public HDFSContentStore(String hdfsStoreLocation) {
        try {
            fileSystem = FileSystem.get(new URI(hdfsStoreLocation), new Configuration());
            LOGGER.info("HDFS file system: {} ({}).", fileSystem.getClass(),fileSystem.getUri());
        } catch (Exception e) {
            //TODO Vince : trouver un moyen plus élégant d'alimenter le contexte
            Map<String, Object> context = new HashMap<>();
            context.put("location", hdfsStoreLocation);
            throw new TDPException(DataSetErrorCodes.UNABLE_TO_CONNECT_TO_HDFS, e, context);
        }
    }

    private static Path getPath(DataSetMetadata dataSetMetadata) {
        return new Path(HDFS_DIRECTORY + dataSetMetadata.getId());
    }

    @Override
    public void storeAsRaw(DataSetMetadata dataSetMetadata, InputStream dataSetContent) {
        try (FSDataOutputStream outputStream = fileSystem.create(getPath(dataSetMetadata))) {
            IOUtils.copy(dataSetContent, outputStream);
            outputStream.flush();
        } catch (IOException e) {
            //TODO Vince : trouver un moyen plus élégant d'alimenter le contexte
            Map<String, Object> context = new HashMap<>();
            context.put("id", dataSetMetadata.getId());
            throw new TDPException(DataSetErrorCodes.UNABLE_TO_STORE_DATASET_CONTENT, e, context);
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
            LOGGER.warn("File '{}' does not exist.",getPath(dataSetMetadata));
            return new ByteArrayInputStream(new byte[0]);
        }
    }

    @Override
    public void delete(DataSetMetadata dataSetMetadata) {
        try {
            fileSystem.delete(getPath(dataSetMetadata), true);
        } catch (IOException e) {
            //TODO Vince : trouver un moyen plus élégant d'alimenter le contexte
            Map<String, Object> context = new HashMap<>();
            context.put("id", dataSetMetadata.getId());
            throw new TDPException(DataSetErrorCodes.UNABLE_TO_DELETE_DATASET,e, context);
        }
    }

    @Override
    public void clear() {
        try {
            fileSystem.delete(new Path(HDFS_DIRECTORY), true);
        } catch (IOException e) {
            throw new TDPException(DataSetErrorCodes.UNABLE_TO_CLEAR_DATASETS, e);
        }
    }
}
