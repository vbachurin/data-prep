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

import java.util.Collection;
import java.util.Collections;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.preparation.Identifiable;

@Component
@ConditionalOnProperty(name = "lock.resource.store", havingValue = "none")
public class NoOpLockedResourceRepository implements LockedResourceRepository {

    @Override
    public LockedResource tryLock(Identifiable resource, String userId) {
        return new LockedResource(resource.getId(), userId, 0);
    }

    @Override
    public LockedResource tryUnlock(Identifiable resource, String userId) {
        return null;
    }

    @Override
    public LockedResource get(Identifiable resource) {
        return null;
    }

    @Override
    public Collection<LockedResource> listAll() {
        return Collections.emptyList();
    }

    @Override
    public Collection<LockedResource> listByUser(String userId) {
        return Collections.emptyList();
    }

    @Override
    public void clear() {

    }

    @Override
    public void remove(Identifiable resource) {

    }

    @Override
    public boolean lockOwned(LockedResource lockedResource, String userId) {
        return true;
    }

    @Override
    public boolean lockReleased(LockedResource lockedResource) {
        return true;
    }
}
