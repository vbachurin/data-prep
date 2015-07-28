package org.talend.dataprep.api.preparation;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Javabean used to wrap a list of Action in json.
 */
public class Actions {

    /** The list of actions. */
    @JsonProperty("actions")
    private List<Action> actions = new LinkedList<>();

    /**
     * @return the list of actions.
     */
    public List<Action> getActions() {
        return actions;
    }

    /**
     * @param actions the list of actions to set.
     */
    public void setActions(List<Action> actions) {
        this.actions = actions;
    }
}
