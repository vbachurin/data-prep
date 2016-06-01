// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// https://github.com/Talend/data-prep/blob/master/LICENSE
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package org.talend.dataprep.api.org.talend.dataprep.api.export;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.talend.dataprep.validation.OneNotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Parameter for dataset/preparation format
 */
@OneNotBlank({ "preparationId", "datasetId" })
public class ExportParameters {

    /** The export format. */
    @NotNull
    private String exportType;

    /** The preparation id to format. If this is null, datasetId must be set. */
    private String preparationId;

    /** The step id to format at a specific state. By default preparation head version is exported. */
    private String stepId = "head";

    /** The dataset id to format. If this is null, preparationId must be set. */
    private String datasetId;

    private String exportName;

    private Map<String, String> arguments = new HashMap<>();

    @JsonProperty("filter")
    @JsonRawValue
    private Object filter;

    @JsonProperty("outFilter")
    @JsonRawValue
    private Object outFilter;

    public String getExportType() {
        return exportType;
    }

    public void setExportType(String format) {
        this.exportType = format;
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

    public Map<String, String> getArguments() {
        return arguments;
    }

    public void setArguments(Map<String, String> arguments) {
        this.arguments = arguments;
    }

    public String getExportName() {
        return exportName;
    }

    public void setExportName(String exportName) {
        this.exportName = exportName;
    }

    /**
     * @return The filter (as raw JSON) for the export.
     * @see org.talend.dataprep.api.filter.FilterService
     */
    @JsonRawValue
    public String getFilter() {
        return filter == null ? null : filter.toString();
    }

    /**
     * @param filter The filter (as raw JSON) for the export.
     * @see org.talend.dataprep.api.filter.FilterService
     */
    public void setFilter(JsonNode filter) {
        if (filter == null || filter.isNull()) {
            this.filter = null;
        } else {
            this.filter = filter;
        }
    }

    @JsonRawValue
    public String getOutFilter() {
        return outFilter == null ? null : outFilter.toString();
    }

    public void setOutFilter(JsonNode outFilter) {
        if (outFilter == null || outFilter.isNull()) {
            this.outFilter = null;
        } else {
            this.outFilter = outFilter;
        }
    }

    @Override
    public String toString() {
        if (preparationId != null) {
            return preparationId;
        } else {
            return datasetId;
        }
    }
}
