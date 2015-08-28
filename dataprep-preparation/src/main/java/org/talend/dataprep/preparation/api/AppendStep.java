package org.talend.dataprep.preparation.api;

import java.util.ArrayList;
import java.util.List;

import org.talend.dataprep.api.preparation.Action;

public class AppendStep {
    private String insertionStepId;

    private List<Action> actions = new ArrayList<>(1);

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    public String getInsertionStepId() {
        return insertionStepId;
    }

    public void setInsertionStepId(String insertionStepId) {
        this.insertionStepId = insertionStepId;
    }
}
