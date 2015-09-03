package org.talend.dataprep.lock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;

/**
 * Factory used to generate locks. This class is used to generate prototype DistributedLock and hide the hazelcast
 * implementation.
 */
@Component
public class LockFactory {

    /** The Hazel cast instance. */
    @Autowired
    private HazelcastInstance hci;

    /**
     * @param id the if where to put the lock.
     * @return a distributed lock.
     */
    public DistributedLock getLock(String id) {
        final ILock lock = hci.getLock(id);
        return new DistributedLock(id, lock);
    }

}
