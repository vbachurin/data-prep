package org.talend.dataprep.lock;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.talend.dataprep.configuration.HazelcastSetup;

@Component
@ConditionalOnMissingBean(HazelcastSetup.class)
public class NoOpLockFactory implements LockFactory {

    @Override
    public DistributedLock getLock(String id) {
        return new NoOpDistributedLock(id);
    }

    private static class NoOpDistributedLock implements DistributedLock {

        private final String id;

        public NoOpDistributedLock(String id) {
            this.id = id;
        }

        @Override
        public void lock() {
            // No op: this class is used when no lock is needed.
        }

        @Override
        public void unlock() {
            // No op: this class is used when no lock is needed.
        }

        @Override
        public String getKey() {
            return id;
        }
    }
}
