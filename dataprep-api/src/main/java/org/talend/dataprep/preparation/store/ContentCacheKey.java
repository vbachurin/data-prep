package org.talend.dataprep.preparation.store;

/**
 * Content cache key used to group all information needed by the cache.
 */
public class ContentCacheKey {

    /** Constant value for the full dataset. */
    private static final String FULL_DATASET = "full";
    /** The preparation id. */
    private String preparationId;
    /** The optional step id. */
    private String stepId;

    /** The optional sample size ('full' if empty). */
    private String sample;

    /**
     * Constructor without sample size.
     *
     * @param preparationId The preparation id.
     * @param stepId The optional step id.
     */
    public ContentCacheKey(String preparationId, String stepId) {
        this.preparationId = preparationId;
        this.stepId = stepId;
        this.sample = FULL_DATASET;
    }

    /**
     * Full constructor.
     *
     * @param preparationId The preparation id.
     * @param stepId The optional step id.
     * @param sample The optional sample size.
     */
    public ContentCacheKey(String preparationId, String stepId, Long sample) {
        this(preparationId, stepId);
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
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return "ContentCacheKey{" + "preparationId='" + preparationId + '\'' + ", stepId='" + stepId + '\'' + ", sample='"
                + sample + '\'' + '}';
    }
}
