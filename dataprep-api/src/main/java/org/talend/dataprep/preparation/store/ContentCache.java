package org.talend.dataprep.preparation.store;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

/**
 * A component to hold versions of a preparation at different steps. Each implementation may implement different
 * eviction strategies that do not need to surface here.
 */
public interface ContentCache {

    /**
     * Check whether a cached content exists for given <code>preparationId</code> at step <code>stepId</code>.
     * 
     * @param preparationId A non-null {@link org.talend.dataprep.api.preparation.Preparation preparation} id.
     * @param stepId A non-null {@link org.talend.dataprep.api.preparation.Step step} id.
     * @return <code>true</code> if cache holds content for given parameters, <code>false</code> otherwise.
     */
    boolean has(String preparationId, String stepId);

    /**
     * Returns the cached content for given <code>preparationId</code> at step <code>stepId</code>
     * 
     * @param preparationId A non-null {@link org.talend.dataprep.api.preparation.Preparation preparation} id.
     * @param stepId A non-null {@link org.talend.dataprep.api.preparation.Step step} id.
     * @return The cached content for given parameters, never <code>null</code>.
     * @throws IllegalArgumentException If no cache can be found for given parameters.
     * @see #has(String, String)
     */
    InputStream get(String preparationId, String stepId);

    /**
     * Allow callers to create an entry in cache for given <code>preparationId</code> at step <code>stepId</code>.
     * Please note content is not passed in parameters but return of this method also callers to write in entry.
     * 
     * @param preparationId A non-null {@link org.talend.dataprep.api.preparation.Preparation preparation} id.
     * @param stepId A non-null {@link org.talend.dataprep.api.preparation.Step step} id.
     * @param timeToLive The {@link org.talend.dataprep.preparation.store.HDFSContentCache.TimeToLive TTL} for the new
     * cache entry.
     * @return A {@link OutputStream output stream} to be used to write content in cache entry
     */
    OutputStream put(String preparationId, String stepId, TimeToLive timeToLive);

    /**
     * Mark cache entry as invalid for given <code>preparationId</code> at step <code>stepId</code>. After this method
     * completes, {@link #has(String, String)} must immediately return <code>false</code>.
     * 
     * @param preparationId A non-null {@link org.talend.dataprep.api.preparation.Preparation preparation} preparationId.
     * @param stepId A non-null {@link org.talend.dataprep.api.preparation.Step step} preparationId.
     */
    void evict(String preparationId, String stepId);

    /**
     * Removes all content cached by this {@link ContentCache cache}.
     */
    void clear();

    /**
     * Configure how long a cache entry may exist in cache.
     */
    enum TimeToLive {
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
