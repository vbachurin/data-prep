package org.talend.dataprep.api.dataset;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the lifecycle of the data set (information that can change over time).
 */
public class DataSetLifecycle {

    @JsonProperty("contentAnalyzed")
    private boolean contentAnalyzed;

    @JsonProperty("schemaAnalyzed")
    private boolean schemaAnalyzed;

    @JsonProperty("qualityAnalyzed")
    private boolean qualityAnalyzed;

    /**
     * Changes the information on content indexed (is the data set content ready to be queried).
     * 
     * @param contentAnalyzed The new value for the information.
     */
    public void contentIndexed(boolean contentAnalyzed) {
        this.contentAnalyzed = contentAnalyzed;
    }

    /**
     * @return <code>true</code> if data set content was indexed and can be queried, <code>false</code> otherwise.
     */
    public boolean contentIndexed() {
        return contentAnalyzed;
    }

    /**
     * Changes the information on schema analysis (is the data set content analyzed so column names and types are
     * available).
     * 
     * @param schemaAnalyzed The new value for the information.
     */
    public void schemaAnalyzed(boolean schemaAnalyzed) {
        this.schemaAnalyzed = schemaAnalyzed;
    }

    /**
     * @return <code>true</code> if data set content was analyzed and column types were correctly inferred from values,
     * <code>false</code> otherwise.
     * @see RowMetadata
     */
    public boolean schemaAnalyzed() {
        return schemaAnalyzed;
    }

    /**
     * Changes the information on columns quality (is information about number of valid, invalid, empty values available
     * or not).
     *
     * @param qualityAnalyzed The new value for the information.
     */
    public void qualityAnalyzed(boolean qualityAnalyzed) {
        this.qualityAnalyzed = qualityAnalyzed;
    }

    /**
     * @return <code>true</code> if data set column values were analyzed and quality information is available,
     * <code>false</code> otherwise.
     */
    public boolean qualityAnalyzed() {
        return qualityAnalyzed;
    }

}
