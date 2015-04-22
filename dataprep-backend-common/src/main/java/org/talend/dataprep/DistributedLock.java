// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprep;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;

/**
 * Basic distributed Lock implementation for locking. This implementation relies on Hazelcast but it was created to
 * avoid Hazelcast dependencies in all the Talend classes. Use ApplicationContext.getBean(DistributedLock.class, key) to
 * get an instance of the bean for the given key.
 *
 */
@Component
@Scope("prototype")
public class DistributedLock {

    @Autowired
    HazelcastInstance hci;

    private String lockKey;

    private ILock lock;

    /**
     * create a distributed lock based on the key param.
     * 
     * @param key, unique name of the lock.
     */
    private DistributedLock(String key) {
        this.lockKey = key;
    }

    @PostConstruct
    private void createLock() {
        lock = hci.getLock(lockKey);
    }

    /**
     * Acquires the lock.
     * 
     * If the lock is not available then the current thread becomes disabled for thread scheduling purposes and lies
     * dormant until the lock has been acquired.
     * 
     * Implementation Considerations
     * 
     * A Lock implementation may be able to detect erroneous use of the lock, such as an invocation that would cause
     * deadlock, and may throw an (unchecked) exception in such circumstances. The circumstances and the exception type
     * must be documented by that Lock implementation.
     * 
     * Specified by: lock() in Lock
     */
    public void lock() {
        lock.lock();
    }

    /**
     * Releases the lock.
     */
    public void unlock() {
        lock.unlock();
    }

    /**
     * Getter for key used for the lock.
     * 
     * @return the key used for the lock
     */
    public String getKey() {
        return lockKey;
    }

}
