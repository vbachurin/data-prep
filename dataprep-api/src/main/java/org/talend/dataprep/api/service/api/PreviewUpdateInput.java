package org.talend.dataprep.api.service.api;

import org.talend.dataprep.api.preparation.Action;

import java.util.List;

public class PreviewUpdateInput {
    private Action action;
    private List<Integer> tdpIds;
    private String currentStepId;
    private String updateStepId;
    private String preparationId;

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public List<Integer> getTdpIds() {
        return tdpIds;
    }

    public void setTdpIds(List<Integer> tdpIds) {
        this.tdpIds = tdpIds;
    }

    public String getCurrentStepId() {
        return currentStepId;
    }

    public void setCurrentStepId(String currentStepId) {
        this.currentStepId = currentStepId;
    }

    public String getUpdateStepId() {
        return updateStepId;
    }

    public void setUpdateStepId(String updateStepId) {
        this.updateStepId = updateStepId;
    }

    public String getPreparationId() {
        return preparationId;
    }

    public void setPreparationId(String preparationId) {
        this.preparationId = preparationId;
    }
}
