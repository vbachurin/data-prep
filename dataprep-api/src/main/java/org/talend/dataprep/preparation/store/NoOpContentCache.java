package org.talend.dataprep.preparation.store;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.output.NullOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/**
 * An implementation of {@link ContentCache} that holds no content. This is mainly an implementation to replace an
 * actual content cache in case configuration does not include HDFS configuration.
 * @see org.talend.dataprep.configuration.HDFS
 * @see HDFSContentCache
 */
@Component
@ConditionalOnMissingBean(ContentCache.class)
public class NoOpContentCache implements ContentCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(NoOpContentCache.class);

    public NoOpContentCache() {
        LOGGER.info("Using content cache: {}", this.getClass().getName());
    }

    @Override
    public boolean has(String preparationId, String stepId) {
        return false;
    }

    @Override
    public InputStream get(String preparationId, String stepId) {
        return new ByteArrayInputStream(new byte[0]);
    }

    @Override
    public OutputStream put(String preparationId, String stepId, HDFSContentCache.TimeToLive timeToLive) {
        return new NullOutputStream();
    }

    @Override
    public void evict(String preparationId, String stepId) {
        // Nothing to do.
    }
}
