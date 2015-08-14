package org.talend.dataprep.preparation.store;

import org.talend.dataprep.api.preparation.Preparation;

/**
 * Content cache key used to group all information needed by the cache.
 */
public class ContentCacheKey {

    /** Constant value for the full dataset. */
    private static final String FULL_DATASET = "full";

    /** The dataset id. */
    private String datasetId;
    /** The preparation id. */
    private String preparationId;
    /** The optional step id. */
    private String stepId;
    /** The optional sample size ('full' if empty). */
    private String sample;

    /**
     * Create a content cache key that only matches the given dataset id.
     * 
     * @param datasetId the dataset id.
     */
    public ContentCacheKey(String datasetId) {
        this.datasetId = datasetId;
        this.preparationId = null;
        this.stepId = null;
        this.sample = "";
    }

    /**
     * Constructor for a preparation and a stepid.
     *
     * @param preparation The preparation to build the cache key from.
     * @param stepId The optional step id.
     */
    public ContentCacheKey(Preparation preparation, String stepId) {
        this.preparationId = preparation.getId();
        this.datasetId = preparation.getDataSetId();
        this.stepId = stepId;
        this.sample = FULL_DATASET;
    }

    /**
     * Full constructor.
     *
     * @param preparation The preparation to build the cache key from.
     * @param stepId The optional step id.
     * @param sample The optional sample size.
     */
    public ContentCacheKey(Preparation preparation, String stepId, Long sample) {
        this(preparation, stepId);
        if (sample != null) {
            this.sample = String.valueOf(sample);
        }
    }

    /**
     * @return the PreparationId
     */
    public String getPreparationId() {
        return preparationId;
    }

    /**
     * @return the StepId
     */
    public String getStepId() {
        return stepId;
    }

    /**
     * @return the Sample
     */
    public String getSample() {
        return sample;
    }

    /**
     * @return the DatasetId
     */
    public String getDatasetId() {
        return datasetId;
    }

    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return "ContentCacheKey{" + "preparationId='" + preparationId + '\'' + ", stepId='" + stepId + '\'' + ", sample='"
                + sample + '\'' + '}';
    }
}
