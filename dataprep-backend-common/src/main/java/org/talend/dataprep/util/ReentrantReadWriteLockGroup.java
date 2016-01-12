package org.talend.dataprep.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A class containing a group of reentrantReadWriteLock.
 */
public class ReentrantReadWriteLockGroup {

    /**
     * A map of reentrant lock associating to each string a reentrant lock.
     */
    private ConcurrentHashMap<String, ReentrantReadWriteLock> locks = new ConcurrentHashMap<>();

    /**
     * Return the ReentrantReadWriteLock associated with the specified string <tt>id</tt>.
     * @param id the specified string
     * @return the ReentrantReadWriteLock associated with the specified string <tt>id</tt>
     */
    public ReentrantReadWriteLock getLock(String id){
        ReentrantReadWriteLock lock = locks.get(id);
        if (lock == null) {
            locks.putIfAbsent(id, new ReentrantReadWriteLock());
        }
        lock = locks.get(id);
        return lock;
    }

}
