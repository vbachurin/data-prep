package org.talend.dataprep.api.preparation;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.annotation.Transient;
import org.talend.dataprep.transformation.api.action.DataSetMetadataAction;
import org.talend.dataprep.transformation.api.action.DataSetRowAction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("action")
public class Action {

    public static final DataSetRowAction IDLE_ROW_ACTION = (row, context) -> {};

    public static final DataSetMetadataAction IDLE_METADATA_ACTION = (metadata, context) -> {};

    @Transient
    private final transient DataSetRowAction rowAction;

    @Transient
    private final transient DataSetMetadataAction metadataAction;

    private String action;

    private Map<String, String> parameters = new HashMap<>(1);

    public Action() {
        rowAction = IDLE_ROW_ACTION;
        metadataAction = IDLE_METADATA_ACTION;
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

    @JsonIgnore(true)
    @Transient
    public DataSetRowAction getRowAction() {
        return rowAction;
    }

    @JsonIgnore(true)
    @Transient
    public DataSetMetadataAction getMetadataAction() {
        return metadataAction;
    }

    public static class Builder {

        private DataSetRowAction rowAction = IDLE_ROW_ACTION;

        private DataSetMetadataAction metadataAction = IDLE_METADATA_ACTION;

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
