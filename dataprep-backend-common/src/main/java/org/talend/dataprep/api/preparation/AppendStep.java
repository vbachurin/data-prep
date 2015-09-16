package org.talend.dataprep.api.preparation;

import java.util.ArrayList;
import java.util.List;

public class AppendStep {
    private StepDiff diff = new StepDiff();

    private List<Action> actions = new ArrayList<>(1);

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    public void setDiff(StepDiff diff) {
        this.diff = diff;
    }

    public StepDiff getDiff() {
        return diff;
    }
}
