package org.talend.dataprep.api.service.api;

import java.util.List;

/**
 * Bean that models a preview on an "Diff action" request.
 */
public class PreviewDiffInput {

    private String preparationId;
    private String currentStepId;
    private String previewStepId;
    private List<Integer> tdpIds;

    /** The sample size (null means full dataset/preparation). */
    private Long sample;

    /**
     * @return the PreparationId
     */
    public String getPreparationId() {
        return preparationId;
    }

    /**
     * @param preparationId the preparationId to set.
     */
    public void setPreparationId(String preparationId) {
        this.preparationId = preparationId;
    }

    /**
     * @return the CurrentStepId
     */
    public String getCurrentStepId() {
        return currentStepId;
    }

    /**
     * @param currentStepId the currentStepId to set.
     */
    public void setCurrentStepId(String currentStepId) {
        this.currentStepId = currentStepId;
    }

    /**
     * @return the PreviewStepId
     */
    public String getPreviewStepId() {
        return previewStepId;
    }

    /**
     * @param previewStepId the previewStepId to set.
     */
    public void setPreviewStepId(String previewStepId) {
        this.previewStepId = previewStepId;
    }

    /**
     * @return the TdpIds
     */
    public List<Integer> getTdpIds() {
        return tdpIds;
    }

    /**
     * @param tdpIds the tdpIds to set.
     */
    public void setTdpIds(List<Integer> tdpIds) {
        this.tdpIds = tdpIds;
    }

    /**
     * @return the Sample
     */
    public Long getSample() {
        return sample;
    }

    /**
     * @param sample the sample to set.
     */
    public void setSample(Long sample) {
        this.sample = sample;
    }
}
