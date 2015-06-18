package org.talend.dataprep.preparation.store;

import java.io.InputStream;

/**
 * A component to holds versions of a preparation at different steps. Each implementation may implement different
 * eviction strategies that do not need to surface here.
 */
public interface ContentCache {

    /**
     * Check whether a cached content exists for given <code>preparationId</code> at step <code>stepId</code>.
     * @param preparationId A non-null {@link org.talend.dataprep.api.preparation.Preparation preparation} id.
     * @param stepId A non-null {@link org.talend.dataprep.api.preparation.Step step} id.
     * @return <code>true</code> if cache holds content for given parameters, <code>false</code> otherwise.
     */
    boolean has(String preparationId, String stepId);

    /**
     * Returns the cached content for given <code>preparationId</code> at step <code>stepId</code>
     * @param preparationId A non-null {@link org.talend.dataprep.api.preparation.Preparation preparation} id.
     * @param stepId A non-null {@link org.talend.dataprep.api.preparation.Step step} id.
     * @return The cached content for given parameters, never <code>null</code>.
     * @throws IllegalArgumentException If no cache can be found for given parameters.
     * @see #has(String, String)
     */
    InputStream get(String preparationId, String stepId);
}
