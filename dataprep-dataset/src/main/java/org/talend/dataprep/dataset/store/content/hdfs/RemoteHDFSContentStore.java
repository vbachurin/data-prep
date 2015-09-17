package org.talend.dataprep.dataset.store.content.hdfs;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.api.dataset.location.HdfsLocation;
import org.talend.dataprep.dataset.exception.DataSetErrorCodes;
import org.talend.dataprep.dataset.store.content.DataSetContentStore;
import org.talend.dataprep.dataset.store.content.DataSetContentStoreAdapter;
import org.talend.dataprep.exception.TDPException;

/**
 * Remote dataset content store on HDFS.
 */
@Component("ContentStore#hdfs")
public class RemoteHDFSContentStore extends DataSetContentStoreAdapter {

    /** This class' logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteHDFSContentStore.class);

    @Override
    public InputStream getAsRaw(DataSetMetadata dataSetMetadata) {

        // defensive programming
        if (!(dataSetMetadata.getLocation() instanceof HdfsLocation)) {
            throw new IllegalArgumentException(this.getClass().getName() + " does not manage " + dataSetMetadata.getLocation());
        }

            // opens the location
        HdfsLocation location = (HdfsLocation) dataSetMetadata.getLocation();
        URI uri = URI.create(location.getUrl());
        Configuration conf = new Configuration();

        try {
            FileSystem file = FileSystem.get(uri, conf);
            return file.open(new Path(uri));

        } catch (IOException e) {
            LOGGER.error("error reading remote HDFS dataset {}", location, e);
            throw new TDPException(DataSetErrorCodes.UNABLE_TO_READ_REMOTE_DATASET_CONTENT, e);
        }
    }

    /**
     * @see DataSetContentStore#storeAsRaw(DataSetMetadata, InputStream)
     */
    @Override
    public void storeAsRaw(DataSetMetadata dataSetMetadata, InputStream dataSetContent) {
        // nothing to do here since the dataset is already stored
    }

    /**
     * @see DataSetContentStore#delete(DataSetMetadata)
     */
    @Override
    public void delete(DataSetMetadata dataSetMetadata) {
        // nothing to do here
        LOGGER.warn("delete called on a remote http content store... (stack trace is informative)", new Exception());
    }

    /**
     * @see DataSetContentStore#clear()
     */
    @Override
    public void clear() {
        // nothing to do here...
        LOGGER.warn("clear called on a remote http content store... (stack trace is informative)", new Exception());
    }
}
