package org.talend.dataprep.preparation.store;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.talend.dataprep.exception.CommonErrorCodes;
import org.talend.dataprep.exception.TDPException;

@Component
@ConditionalOnBean(FileSystem.class)
public class HDFSContentCache implements ContentCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(HDFSContentCache.class);

    @Autowired
    FileSystem fileSystem;

    private static Path getPath(String preparationId, String stepId) {
        return new Path("preparations/" + preparationId + "/" + stepId);
    }

    @Override
    public boolean has(String preparationId, String stepId) {
        try {
            final Path path = getPath(preparationId, stepId);
            return fileSystem.exists(path);
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public InputStream get(String preparationId, String stepId) {
        try {
            final Path path = getPath(preparationId, stepId);
            return fileSystem.open(path);
        } catch (IOException e) {
            throw new TDPException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
        }
    }

    @Override
    public OutputStream put(String preparationId, String stepId) {
        try {
            // TODO Configure TTL
            final Path path = getPath(preparationId, stepId);
            path.suffix(String.valueOf(System.currentTimeMillis() + 10000)); // TTL = 10s
            final boolean created = fileSystem.createNewFile(path);
            if (!created) {
                LOGGER.error("Unable to create cache for {} at step {}", preparationId, stepId);
            }
            return fileSystem.create(path);
        } catch (IOException e) {
            throw new TDPException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
        }
    }

    /**
     * A clean up process that starts a minute after the previous ended.
     */
    @Scheduled(fixedDelay = 60000)
    public void janitor() {
        try {
            final long start = System.currentTimeMillis();
            LOGGER.debug("Janitor process started @ {}.", start);
            final FileStatus[] statuses = fileSystem.listStatus(new Path("/preparations"), path -> {
                final String suffix = StringUtils.substringAfterLast(path.getName(), ".");
                final long time = Long.parseLong(suffix);
                return time > start;
            });
            int deletedCount = 0;
            for (FileStatus status : statuses) {
                try {
                    fileSystem.delete(status.getPath(), true);
                    deletedCount++;
                } catch (IOException e) {
                    LOGGER.error("Unable to delete '" + status.getPath() + "'.", e);
                }
            }
            LOGGER.debug("Janitor process ended @ {} ({}/{} files successfully deleted).", System.currentTimeMillis(),
                    deletedCount, statuses.length);
        } catch (IOException e) {
            LOGGER.error("Unable to clean up cache.", e);
        }
    }
}
