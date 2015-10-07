package org.talend.dataprep.lock;

/**
 * Factory used to generate locks. This class is used to generate prototype DistributedLock and hide the hazelcast
 * implementation.
 */
public interface LockFactory {

    /**
     * @param id An id for the distributed lock. It is up to the caller to decide any naming rules or for uniqueness of
     * id.
     * @return A {@link DistributedLock lock} to perform platform wide lock operations.
     */
    DistributedLock getLock(String id);

}
