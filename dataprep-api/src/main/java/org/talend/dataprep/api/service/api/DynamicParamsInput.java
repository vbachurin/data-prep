package org.talend.dataprep.api.service.api;

import org.hibernate.validator.constraints.NotBlank;
import org.talend.dataprep.validation.OneNotBlank;

@OneNotBlank({ "preparationId", "datasetId" })
public class DynamicParamsInput {

    /**
     * The preparation id. If this is null, datasetId must be set
     */
    private String preparationId;

    /**
     * The step id. If not provided, this is considered as 'head' version
     */
    private String stepId = "head";
    /**
     * The dataset id. If this is null, preparationId must be set
     */
    private String datasetId;

    /**
     * The dataset id. If this is null, preparationId must be set
     */
    @NotBlank
    private String columnId;

    public String getPreparationId() {
        return preparationId;
    }

    public void setPreparationId(String preparationId) {
        this.preparationId = preparationId;
    }

    public String getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(String datasetId) {
        this.datasetId = datasetId;
    }

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    public String getStepId() {
        return stepId;
    }

    public void setStepId(String stepId) {
        this.stepId = stepId;
    }
}
