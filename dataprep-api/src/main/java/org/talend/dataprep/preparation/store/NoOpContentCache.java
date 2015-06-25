package org.talend.dataprep.preparation.store;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.output.NullOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/**
 * An implementation of {@link ContentCache} that holds on content. This is mainly an implementation to replace an
 * actual content cache in case configuration does not include HDFS configuration.
 * @see org.talend.dataprep.configuration.HDFS
 * @see HDFSContentCache
 */
@Component
@ConditionalOnMissingBean(FileSystem.class)
public class NoOpContentCache implements ContentCache {

    @Override
    public boolean has(String preparationId, String stepId) {
        return false;
    }

    @Override
    public InputStream get(String preparationId, String stepId) {
        return new ByteArrayInputStream(new byte[0]);
    }

    @Override
    public OutputStream put(String preparationId, String stepId) {
        return new NullOutputStream();
    }
}
