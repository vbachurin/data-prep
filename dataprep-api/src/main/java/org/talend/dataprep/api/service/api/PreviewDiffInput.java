package org.talend.dataprep.api.service.api;

import java.util.List;

public class PreviewDiffInput {
    private String preparationId;
    private String currentStepId;
    private String previewStepId;
    private List<Integer> tdpIds;

    public String getPreparationId() {
        return preparationId;
    }

    public void setPreparationId(String preparationId) {
        this.preparationId = preparationId;
    }

    public String getCurrentStepId() {
        return currentStepId;
    }

    public void setCurrentStepId(String currentStepId) {
        this.currentStepId = currentStepId;
    }

    public String getPreviewStepId() {
        return previewStepId;
    }

    public void setPreviewStepId(String previewStepId) {
        this.previewStepId = previewStepId;
    }

    public List<Integer> getTdpIds() {
        return tdpIds;
    }

    public void setTdpIds(List<Integer> tdpIds) {
        this.tdpIds = tdpIds;
    }
}
