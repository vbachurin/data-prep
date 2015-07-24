package org.talend.dataprep.preparation.store;

/**
 * Content cache key used to group all information needed by the cache.
 */
public class ContentCacheKey {

    /** The preparation id. */
    private String preparationId;

    /** The optional step id. */
    private String stepId;

    /** The optional sample size. */
    private Long sample;

    /**
     * Constructor without sample size.
     *
     * @param preparationId The preparation id.
     * @param stepId The optional step id.
     */
    public ContentCacheKey(String preparationId, String stepId) {
        this.preparationId = preparationId;
        this.stepId = stepId;
        this.sample = null;
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
        this.sample = sample;
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
    public Long getSample() {
        return sample;
    }

    @Override
    public String toString() {
        return "ContentCacheKey{" + "preparationId='" + preparationId + '\'' + ", sample=" + sample + '\'' + ", stepId='" + stepId
                + '}';
    }
}
