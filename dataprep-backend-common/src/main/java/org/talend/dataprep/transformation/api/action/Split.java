package org.talend.dataprep.transformation.api.action;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.talend.dataprep.api.DataSetRow;

import java.util.Iterator;
import java.util.Map;

class Split implements Action {

    private String separator;

    private String column;

    private String splitResultColumn;

    @Override
    public void perform(DataSetRow row) {
        String[] split = StringUtils.split(row.get(column), separator, 2);
        if (split != null) {
            row.set(column, split[0]);
            row.set(splitResultColumn, split[1]);
        }
    }

    @Override
    public void init(Iterator<Map.Entry<String, JsonNode>> parameters) {
        while (parameters.hasNext()) {
            Map.Entry<String, JsonNode> currentParameter = parameters.next();
            switch (currentParameter.getKey()) {
            case "column_name":
                column = currentParameter.getValue().getTextValue();
                break;
            case "new_column_name":
                splitResultColumn = currentParameter.getValue().getTextValue();
                break;
            case "separator":
                separator = currentParameter.getValue().getTextValue();
                break;
            default:
                ActionParser.LOGGER
                        .warn("Parameter '" + currentParameter.getKey() + "' is not recognized for " + this.getClass());
            }
        }

    }
}
