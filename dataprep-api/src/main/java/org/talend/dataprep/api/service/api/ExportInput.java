package org.talend.dataprep.api.service.api;

import javax.validation.constraints.NotNull;

public class ExportInput {

    @NotNull
    private Type exportType;

    private String preparationId;

    private String stepId;

    private String datasetId;

    public Type getExportType() {
        return exportType;
    }

    public void setExportType(Type exportType) {
        this.exportType = exportType;
    }

    public String getPreparationId() {
        return preparationId;
    }

    public void setPreparationId(String preparationId) {
        this.preparationId = preparationId;
    }

    public String getStepId() {
        return stepId;
    }

    public void setStepId(String stepId) {
        this.stepId = stepId;
    }

    public String getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(String datasetId) {
        this.datasetId = datasetId;
    }

    enum Type {
        CSV,
        XLS,
        TABLEAU
    }
}
