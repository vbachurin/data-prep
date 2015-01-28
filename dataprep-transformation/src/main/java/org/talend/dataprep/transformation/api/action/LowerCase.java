package org.talend.dataprep.transformation.api.action;

import org.codehaus.jackson.JsonNode;
import org.talend.dataprep.transformation.api.DataSetRow;

import java.util.Iterator;
import java.util.Map;

class LowerCase implements Action {

    private String column;

    public LowerCase() {
    }

    @Override
    public void perform(DataSetRow row) {
        row.set(column, row.get(column).toLowerCase());
    }

    @Override
    public void init(Iterator<Map.Entry<String, JsonNode>> parameters) {
        while (parameters.hasNext()) {
            Map.Entry<String, JsonNode> currentParameter = parameters.next();
            switch (currentParameter.getKey()) {
                case "column_name":
                    column = currentParameter.getValue().getTextValue();
                    break;
                default:
                    ActionParser.LOGGER.warn("Parameter '" + currentParameter.getKey() + "' is not recognized for " + this.getClass());
            }
        }
    }

    @Override
    public String toString() {
        return "LowerCase{" +
                "column='" + column + '\'' +
                '}';
    }
}
