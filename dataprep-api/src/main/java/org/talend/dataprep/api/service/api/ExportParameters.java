package org.talend.dataprep.api.service.api;

import java.util.Map;

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

    private Map<String, String> arguments;

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

    public Map<String, String> getArguments()
    {
        return arguments;
    }

    public void setArguments( Map<String, String> arguments )
    {
        this.arguments = arguments;
    }
}
