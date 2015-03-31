package org.talend.dataprep.api.preparation;

import java.util.HashMap;
import java.util.Map;

public class Action {

    private String action;

    private Map<String, String> parameters = new HashMap<>(1);

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }
}
