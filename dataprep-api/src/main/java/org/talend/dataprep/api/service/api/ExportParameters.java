package org.talend.dataprep.api.service.api;

import javax.validation.constraints.NotNull;

import org.talend.dataprep.api.service.validation.OneNotBlank;
import org.talend.dataprep.api.type.ExportType;

/**
 * Parameter for dataset/preparation export
 */
@OneNotBlank({ "preparationId", "datasetId" })
public class ExportParameters {

    /**
     * The export type
     */
    @NotNull
    private ExportType exportType;

    /**
     * CSV separator to use
     */
    private Character csvSeparator;

    /**
     * The preparation id to export. If this is null, datasetId must be set
     */
    private String preparationId;

    /**
     * The step id to export at a specific state. If null, the preparation head version will be exported
     */
    private String stepId;

    /**
     * The dataset id to export. If this is null, preparationId must be set
     */
    private String datasetId;

    public ExportType getExportType() {
        return exportType;
    }

    public void setExportType(ExportType exportType) {
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

    public Character getCsvSeparator() {
        return csvSeparator;
    }

    public void setCsvSeparator(char csvSeparator) {
        this.csvSeparator = csvSeparator;
    }
}
