package org.talend.dataprep.preparation.store;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.output.NullOutputStream;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.fs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.preparation.Step;
import org.talend.dataprep.exception.CommonErrorCodes;
import org.talend.dataprep.exception.TDPException;

@Component
@EnableScheduling
@ConditionalOnProperty(name = "hdfs.location")
public class HDFSContentCache implements ContentCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(HDFSContentCache.class);

    @Autowired
    FileSystem fileSystem;

    public HDFSContentCache() {
        LOGGER.info("Using content cache: {}", this.getClass().getName());
    }

    private Path getPath(String preparationId, String stepId) {
        try {
            if ("origin".equalsIgnoreCase(stepId)) {
                stepId = Step.ROOT_STEP.id();
            }
            final Path preparation = new Path("preparations/" + preparationId);
            if (!fileSystem.exists(preparation)) {
                return null;
            }
            final String filteringStepId = stepId;
            final FileStatus[] statuses = fileSystem.listStatus(preparation, p -> {
                return p.getName().startsWith(filteringStepId);
            });
            FileStatus electedFile = null;
            int maxTTL = 0;
            for (FileStatus status : statuses) {
                final String suffix = StringUtils.substringAfterLast(status.getPath().getName(), ".");
                if (Long.parseLong(suffix) > maxTTL) {
                    electedFile = status;
                }
            }
            if (electedFile == null) {
                return null;
            }
            return electedFile.getPath();
        } catch (IOException e) {
            throw new TDPException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
        }
    }

    @Override
    public boolean has(String preparationId, String stepId) {
        final Path path = getPath(preparationId, stepId);
        final boolean exists = path != null;
        if (exists) {
            LOGGER.debug("[{} @{}] Cache hit.", preparationId, stepId);
        } else {
            LOGGER.debug("[{} @{}] Cache miss.", preparationId, stepId);
        }
        return exists;
    }

    @Override
    public InputStream get(String preparationId, String stepId) {
        try {
            final Path path = getPath(preparationId, stepId);
            if (path == null) {
                throw new IllegalArgumentException("No cache for preparation #" + preparationId + " @ " + stepId);
            }
            return fileSystem.open(path);
        } catch (IOException e) {
            throw new TDPException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
        }
    }

    @Override
    public OutputStream put(String preparationId, String stepId, TimeToLive timeToLive) {
        try {
            if ("head".equals(stepId)) {
                throw new IllegalArgumentException("Illegal shortcut for preparation step '" + stepId + "'.");
            }
            LOGGER.info("[{} @{}] Cache add.", preparationId, stepId);
            // Adds suffix for time to live checks
            final Path preparation = new Path("preparations/" + preparationId + "/" + stepId);
            final Path path = preparation.suffix("." + String.valueOf(System.currentTimeMillis() + timeToLive.getTime()));
            final boolean created = fileSystem.createNewFile(path);
            if (!created) {
                LOGGER.error("[{} @{}] Unable to create cache.", preparationId, stepId);
                return new NullOutputStream();
            }
            return fileSystem.create(path);
        } catch (IOException e) {
            throw new TDPException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
        }
    }

    @Override
    public void evict(String preparationId, String stepId) {
        try {
            LOGGER.debug("[{} @{}] Evict.", preparationId, stepId);
            final Path path = getPath(preparationId, stepId);
            if (path == null) {
                LOGGER.debug("[{} @{}] Evict failed: file already deleted.", preparationId, stepId);
                return;
            }
            fileSystem.rename(path, path.suffix(".0"));
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
            if (!fileSystem.exists(new Path("preparations/"))) {
                LOGGER.info("No cache content to clean.");
            } else {
                final long start = System.currentTimeMillis();
                LOGGER.debug("Janitor process started @ {}.", start);
                final RemoteIterator<LocatedFileStatus> files = fileSystem.listFiles(new Path("preparations/"), true);
                int deletedCount = 0;
                int totalCount = 0;
                while (files.hasNext()) {
                    final LocatedFileStatus fileStatus = files.next();
                    final Path path = fileStatus.getPath();
                    final String suffix = StringUtils.substringAfterLast(path.getName(), ".");
                    final long time = Long.parseLong(StringUtils.isEmpty(suffix) ? "0" : suffix);
                    if (time < start) {
                        try {
                            fileSystem.delete(path, true);
                            deletedCount++;
                        } catch (IOException e) {
                            LOGGER.error("Unable to delete '{}'.", path, e);
                        }
                    }
                    totalCount++;
                }
                LOGGER.debug("Janitor process ended @ {} ({}/{} files successfully deleted).", System.currentTimeMillis(),
                        deletedCount, totalCount);
            }
        } catch (IOException e) {
            LOGGER.error("Unable to clean up cache.", e);
        }
    }

    /**
     * Configure how long a cache entry may exist in cache.
     */
    public enum TimeToLive {
        /**
         * Default time to live for a content in cache (1 hour).
         */
        DEFAULT(TimeUnit.HOURS.toMillis(1)),
        /**
         * Short time to live (short period -> 1 minute).
         */
        SHORT(TimeUnit.MINUTES.toMillis(1)),
        /**
         * Long time to live (long period -> 1 day).
         */
        LONG(TimeUnit.DAYS.toMillis(1));

        private final long time;

        TimeToLive(long time) {
            this.time = time;
        }

        public long getTime() {
            return time;
        }
    }
}
