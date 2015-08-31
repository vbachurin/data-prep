package org.talend.dataprep.api.service.api;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.talend.dataprep.api.preparation.Action;
import org.talend.dataprep.validation.OneNotBlank;

@OneNotBlank({"preparationId", "datasetId"})
public class PreviewAddInput {
    @NotNull
    private Action action;

    @NotEmpty
    private List<Integer> tdpIds;
    private String datasetId;
    private String preparationId;

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public List<Integer> getTdpIds() {
        return tdpIds;
    }

    public void setTdpIds(List<Integer> tdpIds) {
        this.tdpIds = tdpIds;
    }

    public String getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(String datasetId) {
        this.datasetId = datasetId;
    }

    public String getPreparationId() {
        return preparationId;
    }

    public void setPreparationId(String preparationId) {
        this.preparationId = preparationId;
    }
}
