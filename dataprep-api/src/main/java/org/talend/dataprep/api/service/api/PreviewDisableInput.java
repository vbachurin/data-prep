package org.talend.dataprep.api.service.api;

import java.util.List;

import org.talend.dataprep.api.preparation.Action;

public class PreviewDisableInput {
    private String preparationId;
    private String currentStepId;
    private String disableStepId;
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

    public String getDisableStepId() {
        return disableStepId;
    }

    public void setDisableStepId(String disableStepId) {
        this.disableStepId = disableStepId;
    }

    public List<Integer> getTdpIds() {
        return tdpIds;
    }

    public void setTdpIds(List<Integer> tdpIds) {
        this.tdpIds = tdpIds;
    }
}
