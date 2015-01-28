package org.talend.dataprep.transformation.api.action;

import org.codehaus.jackson.JsonNode;
import org.talend.dataprep.transformation.api.DataSetRow;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

class CompositeAction implements Action {

    private final Action[] actions;

    public CompositeAction(Action... actions) {
        this.actions = actions;
    }

    @Override
    public void perform(DataSetRow row) {
        for (Action action : actions) {
            action.perform(row);
        }
    }

    @Override
    public void init(Iterator<Map.Entry<String, JsonNode>> parameters) {
        for (Action action : actions) {
            action.init(parameters);
        }
    }

    @Override
    public String toString() {
        return "CompositeAction{" +
                "actions=" + Arrays.toString(actions) +
                '}';
    }
}
