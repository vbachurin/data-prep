package org.talend.dataprep.transformation.pipeline;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.talend.dataprep.transformation.actions.common.ActionMetadata;

import java.util.List;

@Component
public class NameActionRegistry implements ActionRegistry { // NOSONAR

    @Autowired
    private List<ActionMetadata> actions;

    @Override
    public ActionMetadata get(String name) {
        for (ActionMetadata action : actions) {
            if (action.getName().equals(name)) {
                return action;
            }
        }
        return null;
    }
}
