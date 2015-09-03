package org.talend.dataprep.dataset.store.metadata.file;

import java.io.*;
import java.util.Arrays;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.dataset.exception.DataSetErrorCodes;
import org.talend.dataprep.dataset.store.metadata.DataSetMetadataRepository;
import org.talend.dataprep.dataset.store.metadata.DataSetMetadataRepositoryAdapter;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.TDPExceptionContext;
import org.talend.dataprep.lock.DistributedLock;

/**
 * File system implementation of the DataSetMetadataRepository.
 *
 * Unfortunately, json serialization cannot be used because it's too much API oriented and the default jackson setup
 * ignores RowMetadata from DataSetMetadata. Hence, simple class serialization is performed.
 */
@Component
@ConditionalOnProperty(name = "dataset.metadata.store", havingValue = "file")
public class FileSystemDataSetMetadataRepository extends DataSetMetadataRepositoryAdapter {

    /** This class' logger. */
    private static final Logger LOG = LoggerFactory.getLogger(FileSystemDataSetMetadataRepository.class);

    /** Where to store the dataset metadata */
    @Value("${dataset.metadata.store.file.location}")
    private String storeLocation;

    @PostConstruct
    private void init() {
        getRootFolder().mkdirs();
    }

    /**
     * @see DataSetMetadataRepository#add(DataSetMetadata)
     */
    @Override
    public void add(DataSetMetadata metadata) {

        final File file = getFile(metadata.getId());

        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(file))) {
            output.writeObject(metadata);
        } catch (IOException e) {
            LOG.error("Error saving {}", metadata, e);
            throw new TDPException(DataSetErrorCodes.UNABLE_TO_STORE_DATASET_METADATA, e,
                    TDPExceptionContext.build().put("id", metadata.getId()));
        }
    }

    /**
     * @see DataSetMetadataRepository#get(String)
     */
    @Override
    public DataSetMetadata get(String id) {

        final File file = getFile(id);
        if (!file.exists()) {
            LOG.info("dataset #{} not found in file system", id);
            return null;
        }

        if (!file.canRead()) {
            LOG.info("dataset #{} not available in file system, it is perhaps used by another thread ?", id);
            return null;
        }

        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(file))) {
            return (DataSetMetadata) input.readObject();
        } catch (ClassNotFoundException | IOException e) {
            throw new TDPException(DataSetErrorCodes.UNABLE_TO_READ_DATASET_METADATA, TDPExceptionContext.build().put("id", id));
        }
    }

    /**
     * @see DataSetMetadataRepository#remove(String)
     */
    @Override
    public void remove(String id) {
        final File file = getFile(id);
        if (file.exists()) {
            file.delete();
        }
        LOG.debug("metadata {} successfully deleted", id);
    }

    /**
     * @see DataSetMetadataRepository#size()
     */
    @Override
    public int size() {
        File folder = getRootFolder();
        int size = folder.list().length;
        LOG.debug("found {} datasets metadata", size);
        return size;
    }

    /**
     * @see DataSetMetadataRepository#list()
     */
    @Override
    public Iterable<DataSetMetadata> list() {
        File folder = getRootFolder();
        final Stream<DataSetMetadata> stream = Arrays.stream(folder.listFiles()).map(f -> {

            try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(f))) {
                return (DataSetMetadata) input.readObject();
            } catch (ClassNotFoundException | IOException e) {
                LOG.error("error reading metadata file {}", f.getAbsolutePath(), e);
                throw new TDPException(DataSetErrorCodes.UNABLE_TO_READ_DATASET_METADATA, e);
            }

        });
        return stream::iterator;
    }

    /**
     * @see DataSetMetadataRepository#clear()
     */
    @Override
    public void clear() {
        // Remove all data set (but use lock for remaining asynchronous processes).
        for (DataSetMetadata metadata : list()) {
            final DistributedLock lock = createDatasetMetadataLock(metadata.getId());
            try {
                lock.lock();
                remove(metadata.getId());
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * Return the file that matches the given metadata id.
     * 
     * @param metadataId the metadata id.
     * @return the file where to read/write the metadata.
     */
    private File getFile(String metadataId) {
        return new File(storeLocation + "/metadata/" + metadataId);
    }

    /**
     * @return the metadata root folder.
     */
    private File getRootFolder() {
        return new File(storeLocation + "/metadata/");
    }

}
