package org.talend.dataprep.util;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.junit.Test;

public class ReentrantReadWriteLockGroupTest {

    @Test
    public void ensure_that_consecutive_calls_on_same_string_return_the_same_lock() {
        // given
        ReentrantReadWriteLockGroup locks = new ReentrantReadWriteLockGroup();

        // when
        ReentrantReadWriteLock lock1 = locks.getLock("1");
        ReentrantReadWriteLock lock2 = locks.getLock("1");
        ReentrantReadWriteLock lock3 = locks.getLock("1");

        // then
        org.junit.Assert.assertEquals(lock1, lock2);
        org.junit.Assert.assertEquals(lock1, lock3);
    }

    @Test
    public void ensure_that_calls_with_different_strings_returns_different_locks() {
        // given
        ReentrantReadWriteLockGroup locks = new ReentrantReadWriteLockGroup();

        // when
        ReentrantReadWriteLock lock1 = locks.getLock("1");
        ReentrantReadWriteLock lock2 = locks.getLock("2");
        ReentrantReadWriteLock lock3 = locks.getLock("3");

        // then
        org.junit.Assert.assertNotEquals(lock1, lock2);
        org.junit.Assert.assertNotEquals(lock1, lock3);
        org.junit.Assert.assertNotEquals(lock2, lock3);
    }

}