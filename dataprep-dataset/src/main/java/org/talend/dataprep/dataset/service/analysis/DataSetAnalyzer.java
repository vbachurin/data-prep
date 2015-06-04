package org.talend.dataprep.dataset.service.analysis;

public interface DataSetAnalyzer {

    /**
     * Analyze data available in data set with <code>dataSetId</code> id.
     *
     * @param dataSetId A Data Set id. Implementations are responsible to check if data set still exists at the moment
     * it's called.
     */
    void analyze(String dataSetId);

}
