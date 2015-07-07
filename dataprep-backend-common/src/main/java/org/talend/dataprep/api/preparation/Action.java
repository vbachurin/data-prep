package org.talend.dataprep.api.preparation;

import java.util.HashMap;
import java.util.Map;

import org.talend.dataprep.transformation.api.action.DataSetMetadataAction;
import org.talend.dataprep.transformation.api.action.DataSetRowAction;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("action")
public class Action {

    private final transient DataSetRowAction rowAction;

    private final transient DataSetMetadataAction metadataAction;

    private String action;

    private Map<String, String> parameters = new HashMap<>(1);

    public Action() {
        rowAction = (row, context) -> {};
        metadataAction = (metadata, context) -> {};
    }

    public Action(DataSetRowAction rowAction, DataSetMetadataAction metadataAction) {
        this.rowAction = rowAction;
        this.metadataAction = metadataAction;
    }

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

    public DataSetRowAction getRowAction() {
        return rowAction;
    }

    public DataSetMetadataAction getMetadataAction() {
        return metadataAction;
    }

    public static class Builder {

        private DataSetRowAction rowAction;

        private DataSetMetadataAction metadataAction;

        public static Builder builder() {
            return new Builder();
        }

        public Builder withRow(DataSetRowAction rowAction) {
            this.rowAction = rowAction;
            return this;
        }

        public Builder withMetadata(DataSetMetadataAction metadataAction) {
            this.metadataAction = metadataAction;
            return this;
        }

        public Action build() {
            return new Action(rowAction, metadataAction);
        }

    }
}
