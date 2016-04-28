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

import static org.junit.Assert.*;

import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.talend.dataprep.api.preparation.Identifiable;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { NoOpLockedResourceRepositoryTest.class })
@ComponentScan(basePackages = "org.talend.dataprep")
@TestPropertySource(properties = { "lock.resource.store=none" })
public class NoOpLockedResourceRepositoryTest extends LockedResourceTestUtils {

    @Autowired
    LockedResourceRepository repository;

    @Test
    public void should_lock_resource_lock() {
        String owner = "1";
        String preEmpter = "2";
        Identifiable resource = getFirstResourceType("1");

        LockedResource lockedResource = repository.tryLock(resource, owner);
        LockedResource lockedByPreempter = repository.tryLock(resource, preEmpter);

        assertNotNull(lockedResource);
        assertTrue(repository.lockOwned(lockedResource, owner));
        assertTrue(repository.lockOwned(lockedByPreempter, preEmpter));
        assertNotEquals(lockedByPreempter, lockedResource);

    }

    @Test
    public void should_always_unlock() {
        String owner = "1";
        String preEmpter = "2";
        Identifiable resource = getFirstResourceType("1");

        LockedResource lockedResource = repository.tryLock(resource, owner);
        LockedResource lockedByPreemter = repository.tryUnlock(resource, preEmpter);

        assertNotNull(lockedResource);
        assertNull(lockedByPreemter);
        assertTrue(repository.lockReleased(lockedByPreemter));

    }

    @Test
    public void should_unlock_unlocked_resource() {
        String userId = "1";
        Identifiable resource = getFirstResourceType("1");

        final LockedResource mustBeNull = repository.tryUnlock(resource, userId);

        assertNull(mustBeNull);
    }

    @Test
    public void lock_should_be_reentrant() {
        String lockOwner = "1";
        Identifiable resource = getFirstResourceType("1");

        LockedResource lockedResource = repository.tryLock(resource, lockOwner);
        LockedResource lockedResource2 = repository.tryLock(resource, lockOwner);

        assertNotNull(lockedResource);
        assertNotNull(lockedResource2);
        assertEquals(lockedResource.getUserId(), lockedResource2.getUserId());
        assertEquals(lockedResource.getResourceId(), lockedResource2.getResourceId());
        assertTrue(lockedResource.getExpirationTime() <= lockedResource2.getExpirationTime());
    }

    @Test
    public void should_always_list_empty_collection() {
        String user1 = "1";
        String user2 = "2";
        Identifiable resource = getFirstResourceType("1");
        Identifiable resource2 = getSecondResourceType("2");
        Identifiable resource3 = getSecondResourceType("3");

        LockedResource lockOnResource1 = repository.tryLock(resource, user1);
        LockedResource lockOnResource2 = repository.tryLock(resource2, user2);
        repository.tryLock(resource3, user2);
        repository.tryUnlock(resource3, user2);

        Collection<LockedResource> allLockedResources = repository.listAll();

        assertNotNull(allLockedResources);
        assertEquals(0, allLockedResources.size());
    }

    @Test
    public void should_list_empty_collection_for_resources_locked_by_a_user() {
        String user1 = "1";
        String user2 = "2";
        Identifiable resource = getFirstResourceType("1");
        Identifiable resource2 = getSecondResourceType("2");
        Identifiable resource3 = getSecondResourceType("3");

        LockedResource lockOnResource1 = repository.tryLock(resource, user1);
        LockedResource lockOnResource2 = repository.tryLock(resource2, user2);
        LockedResource lockOnResource3 = repository.tryLock(resource3, user2);

        Collection<LockedResource> allLockedResources = repository.listByUser(user2);

        assertNotNull(allLockedResources);
        assertEquals(0, allLockedResources.size());
    }

    @Test
    public void should_get_null_when_trying_to_get_locked_resource() {
        String user1 = "1";
        Identifiable resource = getFirstResourceType("1");

        LockedResource lockOnResource1 = repository.tryLock(resource, user1);

        LockedResource mustBeNull = repository.get(resource);

        assertNull(mustBeNull);
        assertTrue(repository.lockReleased(mustBeNull));
    }
}