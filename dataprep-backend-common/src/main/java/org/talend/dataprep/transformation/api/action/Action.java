package org.talend.dataprep.transformation.api.action;

import java.util.Iterator;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.talend.dataprep.api.DataSetRow;

public interface Action {

    void perform(DataSetRow row);

    void init(Iterator<Map.Entry<String, JsonNode>> parameters);
}
