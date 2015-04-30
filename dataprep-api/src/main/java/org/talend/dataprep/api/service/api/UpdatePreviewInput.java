package org.talend.dataprep.api.service.api;

import org.talend.dataprep.api.preparation.Action;

import java.util.List;

public class UpdatePreviewInput {
    private String stepId;
    private Action action;
    private List<Integer> tdpIds;
    private String lastActiveStepId;
    private String preparationId;

    public String getStepId() {
        return stepId;
    }

    public void setStepId(String stepId) {
        this.stepId = stepId;
    }

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

    public String getLastActiveStepId() {
        return lastActiveStepId;
    }

    public void setLastActiveStepId(String lastActiveStepId) {
        this.lastActiveStepId = lastActiveStepId;
    }

    public String getPreparationId() {
        return preparationId;
    }

    public void setPreparationId(String preparationId) {
        this.preparationId = preparationId;
    }
}
