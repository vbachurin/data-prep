package org.talend.dataprep.dataset.store.metadata;

import org.springframework.beans.factory.annotation.Autowired;
import org.talend.dataprep.lock.DistributedLock;
import org.talend.dataprep.lock.LockFactory;

/**
 * Base class for all DataSetMetadataRepository implementation.
 */
public abstract class DataSetMetadataRepositoryAdapter implements DataSetMetadataRepository {

    /** Prefix for the shared lock when working on a dataset. */
    private static final String DATASET_LOCK_PREFIX = "dataset#"; //$NON-NLS-1$

    /** The lock factory. */
    @Autowired
    private LockFactory lockFactory;

    /**
     * @see DataSetMetadataRepository#createDatasetMetadataLock(String)
     */
    @Override
    public DistributedLock createDatasetMetadataLock(String id) {
        return lockFactory.getLock(DATASET_LOCK_PREFIX + id);
    }

}
