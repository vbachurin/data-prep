package org.talend.dataprep.preparation.api;

import org.talend.dataprep.preparation.Action;

import java.util.ArrayList;
import java.util.List;

public class AppendStep {
    private List<Action> actions = new ArrayList<>(1);

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }
}
