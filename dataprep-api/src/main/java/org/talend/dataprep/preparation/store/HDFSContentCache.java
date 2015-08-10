package org.talend.dataprep.preparation.store;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.CommonErrorCodes;

@Component
@EnableScheduling
@ConditionalOnProperty(name = "hdfs.location")
public class HDFSContentCache implements ContentCache {

    /** This class' logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(HDFSContentCache.class);

    /** Hdfs cache location. */
    @Autowired
    private FileSystem fileSystem;

    /**
     * Default empty constructor.
     */
    public HDFSContentCache() {
        LOGGER.info("Using content cache: {}", this.getClass().getName());
    }

    /**
     * Return the HDFS {@link Path path} for a preparation at a given step and sample size. The
     * <code>includeEvicted</code> boolean indicates whether {@link #evict(ContentCacheKey) evicted} content should be
     * included in search.
     * 
     * @param key content cache key.
     * @param includeEvicted <code>true</code> if method should also look into evicted content, <code>false</code>
     * otherwise.
     * @param fileSystem The HDFS instance to use for search.
     * @return The {@link Path path} to the content or <code>null</code> if not found.
     */
    static Path getPath(ContentCacheKey key, boolean includeEvicted, FileSystem fileSystem) {
        try {

            String preparationId = key.getPreparationId();
            String stepId = key.getStepId();
            String sample = key.getSample();

            if (preparationId == null) {
                throw new IllegalArgumentException("Preparation id cannot be null.");
            }

            if (key.getStepId() == null) {
                throw new IllegalArgumentException("Step id cannot be null.");
            }

            final Path preparation = new Path("preparations/" + preparationId + '/' + sample);
            if (!fileSystem.exists(preparation)) {
                return null;
            }

            if ("origin".equalsIgnoreCase(stepId)) {
                stepId = Step.ROOT_STEP.id();
            }
            final String filteringStepId = stepId;
            final FileStatus[] statuses = fileSystem.listStatus(preparation, p -> p.getName().startsWith(filteringStepId));
            FileStatus electedFile = null;
            int maxTTL = includeEvicted ? -1 : 0;
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


    /**
     * @see ContentCache#has(ContentCacheKey)
     */
    @Override
    public boolean has(ContentCacheKey key) {
        final Path path = getPath(key, false, fileSystem);
        final boolean exists = path != null;
        if (exists) {
            LOGGER.debug("[{}] Cache hit.", key);
        } else {
            LOGGER.debug("[{}] Cache miss.", key);
        }
        return exists;
    }

    /**
     * @see ContentCache#hasAny(ContentCacheKey)
     */
    @Override
    public boolean hasAny(ContentCacheKey key) {
        boolean exists = false;
        final Path rootFolder = new Path("preparations/" + key.getPreparationId());
        String stepId = key.getStepId();

        try {
            // list all child files (recursive)
            RemoteIterator<LocatedFileStatus> iterator = fileSystem.listFiles(rootFolder, true);
            while (iterator.hasNext()) {
                final Path currentPath = iterator.next().getPath();
                // find the matching step
                if (StringUtils.startsWith(currentPath.getName(), stepId)) {
                    final String suffix = StringUtils.substringAfterLast(currentPath.getName(), ".");
                    // check the TTL validity
                    if (Long.parseLong(suffix) > 0) {
                        exists = true;
                        break;
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.warn("error listing cache entries for preparation {}", key.getPreparationId());
            return false;
        }

        if (exists) {
            LOGGER.debug("[{}] Cache hit.", key);
        } else {
            LOGGER.debug("[{}] Cache miss.", key);
        }
        return exists;
    }

    /**
     * @see ContentCache#get(ContentCacheKey)
     */
    @Override
    public InputStream get(ContentCacheKey key) {
        try {
            final Path path = getPath(key, false, fileSystem);
            if (path == null) {
                throw new IllegalArgumentException(
                        "No cache for preparation #" + key.getPreparationId() + " @ " + key.getStepId());
            }
            return fileSystem.open(path);
        } catch (IOException e) {
            throw new TDPException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
        }
    }

    /**
     * @see ContentCache#put(ContentCacheKey, TimeToLive)
     */
    @Override
    public OutputStream put(ContentCacheKey key, TimeToLive timeToLive) {
        try {

            String preparationId = key.getPreparationId();
            String stepId = key.getStepId();
            String sampleSize = key.getSample();

            if ("head".equals(stepId) || "origin".equals(stepId)) {
                throw new IllegalArgumentException("Illegal shortcut for preparation step '" + stepId + "'.");
            }
            LOGGER.debug("[{}] Cache add.", key);
            // Adds suffix for time to live checks
            final Path preparation = new Path("preparations/" + preparationId + '/' + sampleSize + '/' + stepId);
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

    /**
     * @see ContentCache#evict(ContentCacheKey)
     */
    @Override
    public void evict(ContentCacheKey key) {
        try {
            LOGGER.debug("[{}] Evict.", key);
            final Path path = getPath(key, false, fileSystem);
            if (path == null) {
                LOGGER.debug("[{}] Evict failed: file already deleted.", key);
                return;
            }
            fileSystem.rename(path, path.suffix(".0"));
        } catch (IOException e) {
            throw new TDPException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
        }
    }

    /**
     * @see ContentCache#evictAllEntries(ContentCacheKey)
     */
    @Override
    public void evictAllEntries(ContentCacheKey key) {

        LOGGER.debug("[{}] Evict for all sample size.", key);

        String preparationId = key.getPreparationId();
        final String stepId = key.getStepId();

        try {
            final Path path = new Path("preparations/" + preparationId);
            if (path == null) {
                LOGGER.debug("[{}] Evict failed: file already deleted.", key);
                return;
            }

            RemoteIterator<LocatedFileStatus> iterator = fileSystem.listFiles(path, true);
            while (iterator.hasNext()) {
                final Path currentPath = iterator.next().getPath();
                if (StringUtils.startsWith(currentPath.getName(), stepId)) {
                    fileSystem.rename(currentPath, path.suffix(".0"));
                }
            }

        } catch (IOException e) {
            throw new TDPException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
        }
    }

    /**
     * @see ContentCache#clear()
     */
    @Override
    public void clear() {
        try {
            final Path preparationRootPath = new Path("preparations/");
            if (fileSystem.exists(preparationRootPath)) {
                fileSystem.delete(preparationRootPath, true);
            }
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
                LOGGER.debug("No cache content to clean.");
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
                    if (!suffix.startsWith("nfs")) { // Ignore NFS files (HDFS + NFS? yes, but may happen in local mode).
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
                }
                LOGGER.debug("Janitor process ended @ {} ({}/{} files successfully deleted).", System.currentTimeMillis(),
                        deletedCount, totalCount);
            }
        } catch (IOException e) {
            LOGGER.error("Unable to clean up cache.", e);
        }
    }

}
