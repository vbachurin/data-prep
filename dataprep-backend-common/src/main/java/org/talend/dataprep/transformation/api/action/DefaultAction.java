package org.talend.dataprep.transformation.api.action;

import org.codehaus.jackson.JsonNode;
import org.talend.dataprep.api.DataSetRow;

import java.util.Iterator;
import java.util.Map;

class DefaultAction implements Action {

    @Override
    public void perform(DataSetRow row) {
        // No op.
    }

    @Override
    public void init(Iterator<Map.Entry<String, JsonNode>> parameters) {
        // No op.
    }
}
