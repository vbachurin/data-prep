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

/**
 * Represents a user locked-Resource. It keeps track of the identifier of the locked resource, the user locking it and
 * the expiration time of the lock. The lock will be released when the expiration time is reached.
 */
public class LockedResource {

    /**
     * The identifier of the user who is locking the resource
     */
    private String userId;

    /**
     * The identifier of the locked resource
     */
    private String resourceId;

    /**
     * The time when the lock will be released
     */
    private long expirationTime;

    /**
     * Constructs a locked resource with the specified lock delay
     *
     * @param resourceId the specified resource identifier
     * @param userId the specified user identifier
     * @param delay the specified lock delay
     */
    public LockedResource(String resourceId, String userId, long delay) {
        this.resourceId = resourceId;
        this.userId = userId;
        this.expirationTime = Instant.now().getEpochSecond() + delay;
    }

    /**
     * NO argument constructor for Jackson
     */
    public LockedResource() {

    }

    /**
     *
     * @return the identifier of the user locking the resource
     */
    public String getUserId() {
        return userId;
    }

    /**
     *
     * @return the identifier of the locked resource
     */
    public String getResourceId() {
        return resourceId;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        LockedResource that = (LockedResource) o;

        if (expirationTime != that.expirationTime)
            return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null)
            return false;
        return resourceId != null ? resourceId.equals(that.resourceId) : that.resourceId == null;

    }

    @Override
    public int hashCode() {
        int result = userId != null ? userId.hashCode() : 0;
        result = 31 * result + (resourceId != null ? resourceId.hashCode() : 0);
        result = 31 * result + (int) (expirationTime ^ (expirationTime >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "LockedResource{" +
                "userId='" + userId + '\'' +
                ", resourceId='" + resourceId + '\'' +
                ", expirationTime=" + expirationTime +
                '}';
    }
}
