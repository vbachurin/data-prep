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

/**
 * HDFS implementation of the content cache.
 *
 * Cache entries path look like this : datasets/{id}/preparations/{id}/steps/{stepid}/{sample} so that cache eviction
 * can happen at different level : dataset, preparation or step (sample is irrelevant because it's tied to the step).
 */
@Component
@EnableScheduling
@ConditionalOnProperty(name = "hdfs.location")
public class HDFSContentCache implements ContentCache {

    /** This class' logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(HDFSContentCache.class);

    /** Root folder name. */
    private static final String ROOT = "cache";

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
     * Compute the path for the given key.
     *
     * @param key the cache key entry.
     * @return the HDFS path for the entry key.
     */
    private Path computeEntryPath(ContentCacheKey key) {

        String datasetId = key.getDatasetId();
        String preparationId = key.getPreparationId();
        String stepId = key.getStepId();
        String sample = key.getSample();

        if (datasetId == null) {
            throw new IllegalArgumentException("Dataset id cannot be null.");
        }

        if (preparationId == null) {
            throw new IllegalArgumentException("Preparation id cannot be null.");
        }

        if (key.getStepId() == null) {
            throw new IllegalArgumentException("Step id cannot be null.");
        }

        if ("origin".equalsIgnoreCase(stepId)) {
            stepId = Step.ROOT_STEP.id();
        }

        return new Path(ROOT + "/datasets/" + datasetId + "/preparations/" + preparationId + "/steps/" + stepId + '/' + sample);
    }

    /**
     * @see ContentCache#put(ContentCacheKey, TimeToLive)
     */
    @Override
    public OutputStream put(ContentCacheKey key, TimeToLive timeToLive) {
        try {

            // check the step id value
            String stepId = key.getStepId();
            if ("head".equals(stepId) || "origin".equals(stepId)) {
                throw new IllegalArgumentException("Illegal shortcut for preparation step '" + stepId + "'.");
            }

            Path rootPath = computeEntryPath(key);

            // Adds suffix for time to live checks
            final Path path = rootPath.suffix("." + String.valueOf(System.currentTimeMillis() + timeToLive.getTime()));
            final boolean created = fileSystem.createNewFile(path);
            if (!created) {
                LOGGER.error("[{} @{}] Unable to create cache.", key.getPreparationId(), stepId);
                return new NullOutputStream();
            }

            LOGGER.debug("[{}] Cache add.", key);

            return fileSystem.create(path);

        } catch (IOException e) {
            throw new TDPException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
        }
    }

    /**
     * @see ContentCache#has(ContentCacheKey)
     */
    @Override
    public boolean has(ContentCacheKey key) {

        Path entry = computeEntryPath(key);

        FileStatus[] entries;
        try {
            entries = fileSystem.listStatus(entry.getParent(), p -> p.getName().startsWith(key.getSample()));
        } catch (IOException e) {
            return false;
        }

        final long now = System.currentTimeMillis();
        for (FileStatus status : entries) {
            final String suffix = StringUtils.substringAfterLast(status.getPath().getName(), ".");
            if (Long.parseLong(suffix) > now) {
                LOGGER.debug("[{}] Cache hit.", key);
                return true;
            }
        }

        LOGGER.debug("[{}] Cache miss.", key);
        return false;
    }

    /**
     * @see ContentCache#get(ContentCacheKey)
     */
    @Override
    public InputStream get(ContentCacheKey key) {

        Path entry = computeEntryPath(key);

        FileStatus[] files;
        try {
            files = fileSystem.listStatus(entry.getParent(), p -> p.getName().startsWith(key.getSample()));
        } catch (IOException e) {
            throw new IllegalArgumentException("No cache for preparation #" + key);
        }

        for (FileStatus file : files) {
            final String suffix = StringUtils.substringAfterLast(file.getPath().getName(), ".");
            if (Long.parseLong(suffix) > System.currentTimeMillis()) {
                try {
                    return fileSystem.open(file.getPath());
                } catch (IOException ioe) {
                    throw new TDPException(CommonErrorCodes.UNEXPECTED_EXCEPTION, ioe);
                }
            }
        }
        throw new IllegalArgumentException("No cache for preparation #" + key);
    }


    /**
     * @see ContentCache#evict(ContentCacheKey)
     */
    @Override
    public void evict(ContentCacheKey key) {

        String temp = ROOT + "/datasets";
        if (key.getDatasetId() != null) {
            temp += '/' + key.getDatasetId();

            if (key.getPreparationId() != null) {
                temp += "/preparations/" + key.getPreparationId();

                if (key.getStepId() != null) {
                    temp += "/steps/" + key.getStepId();
                }

            }
            // no need to go up to the sample size if the
        }
        Path path = new Path(temp);

        try {

            // defensive programming
            if (!fileSystem.exists(path)) {
                return;
            }

            final RemoteIterator<LocatedFileStatus> files = fileSystem.listFiles(path, true);
            while (files.hasNext()) {
                final LocatedFileStatus file = files.next();
                fileSystem.rename(file.getPath(), file.getPath().suffix(".0"));
            }

            LOGGER.debug("[{}] Evict.", key);

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
            final Path preparationRootPath = new Path(ROOT + "/datasets/");
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

            if (!fileSystem.exists(new Path(ROOT + "/datasets/"))) {
                LOGGER.debug("No cache content to clean.");
                return;
            }

            final long start = System.currentTimeMillis();
            LOGGER.debug("Janitor process started @ {}.", start);
            final RemoteIterator<LocatedFileStatus> files = fileSystem.listFiles(new Path(ROOT + "/datasets/"), true);
            int deletedCount = 0;
            int totalCount = 0;
            while (files.hasNext()) {
                final LocatedFileStatus fileStatus = files.next();
                final Path path = fileStatus.getPath();
                final String suffix = StringUtils.substringAfterLast(path.getName(), ".");

                // Ignore NFS files (HDFS + NFS? yes, but may happen in local mode).
                if (suffix.startsWith("nfs")) {
                    continue;
                }

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

        } catch (IOException e) {
            LOGGER.error("Unable to clean up cache.", e);
        }
    }

}
