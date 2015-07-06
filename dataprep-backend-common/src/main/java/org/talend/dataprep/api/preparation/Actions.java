package org.talend.dataprep.api.preparation;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Actions {

    @JsonProperty("actions")
    private List<Action> actions = new LinkedList<>();

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }
}
