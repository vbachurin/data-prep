// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// https://github.com/Talend/data-prep/blob/master/LICENSE
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package org.talend.dataprep.lock.store;

import java.time.Instant;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.talend.dataprep.api.preparation.Identifiable;
import org.talend.dataprep.lock.DistributedLock;
import org.talend.dataprep.lock.LockFactory;

public abstract class AbstractLockedResourceRepository implements LockedResourceRepository {

    /** This class' logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractLockedResourceRepository.class);

    /**
     * The default delay before a lock is silently released
     */

    protected long delay;

    /**
     * The locked factory to be set in the concrete class
     */
    protected LockFactory lockFactory;


    @Value("${lock.resource.store.lock.delay:10}")
    public void setDelay(long delay) {
        this.delay = delay;
    }

    @Override
    public LockedResource tryLock(Identifiable resource, String userId) {
        return tryLock(resource, userId, lockFactory);
    }

    @Override
    public LockedResource tryUnlock(Identifiable resource, String userId) {
        return tryUnlock(resource, userId, lockFactory);
    }

    /**
     * @see {@link LockedResourceRepository#tryLock(Identifiable, String)}
     * @param resource the specified identifiable object
     * @param userId the specified user who is requesting the lock of the resource
     * @param lockFactory the lock factory used by this repository
     * @return
     */
    private LockedResource tryLock(Identifiable resource, String userId, LockFactory lockFactory) {
        checkArguments(resource, userId);
        String resourceId = resource.getId();
        DistributedLock lock = lockFactory.getLock(resourceId);
        LockedResource lockedResource;
        lock.lock();
        try {
            lockedResource = get(resource);
            if (lockedResource == null) {
                lockedResource = add(resource, userId);
            } else if (lockOwned(lockedResource, userId) || lockExpired(lockedResource)) {
                remove(resource);
                lockedResource = add(resource, userId);
            } else {
                return lockedResource;
            }
        } finally {
            lock.unlock();
        }

        return lockedResource;
    }

    /**
     * @see {@link LockedResourceRepository#tryUnlock(Identifiable, String)}
     * @param resource the specified identifiable object
     * @param userId the specified user who is requesting the lock of the resource
     * @param lockFactory the lock factory used by this repository
     * @return
     */
    private LockedResource tryUnlock(Identifiable resource, String userId, LockFactory lockFactory) {
        checkArguments(resource, userId);

        String resourceId = resource.getId();
        DistributedLock lock = lockFactory.getLock(resourceId);
        final LockedResource result;
        lock.lock();
        try {
            LockedResource lockedResource = get(resource);
            if (lockedResource == null) {
                result = null;
            } else if (lockOwned(lockedResource, userId) || lockExpired(lockedResource)) {
                remove(resource);
                result = null;
            } else {
                result = lockedResource;
            }
        } finally {
            lock.unlock();
        }

        return result;
    }

    /**
     * Checks whether or not the provided arguments are legal
     * 
     * @param resource
     * @param userId
     */
    private void checkArguments(Identifiable resource, String userId) {
        if (resource == null) {
            LOGGER.warn("A null resource cannot be locked/unlocked...");
            throw new IllegalArgumentException("A null resource cannot be locked");
        }
        if (org.apache.commons.lang.StringUtils.isEmpty(userId)) {
            LOGGER.warn("A null user-identifier cannot lock/unlock a resource...");
            throw new IllegalArgumentException("A null user-identifier cannot be locked");
        }
    }

    @Override
    public boolean lockOwned(LockedResource lockedResource, String userId) {
        final long now = Instant.now().getEpochSecond();
        return lockedResource != null && StringUtils.isNotEmpty(userId) && StringUtils.equals(userId, lockedResource.getUserId())
                && now <= lockedResource.getExpirationTime();
    }

    private boolean lockExpired(LockedResource lockedResource) {
        final long now = Instant.now().getEpochSecond();
        return lockedResource != null && lockedResource.getExpirationTime() < now;
    }

    @Override
    public boolean lockReleased(LockedResource lockedResource) {
        return lockedResource == null;
    }

    /**
     * Adds a resource to the collection of known locked-resource used by this repository.
     * 
     * @param resource the specified identifiable object
     * @param userId the specified user who is requesting the lock of the resource
     * @return the locked resource object that has been added to this repository
     */
    protected abstract LockedResource add(Identifiable resource, String userId);

}
