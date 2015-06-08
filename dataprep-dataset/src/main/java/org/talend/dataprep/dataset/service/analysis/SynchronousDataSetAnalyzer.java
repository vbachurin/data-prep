package org.talend.dataprep.dataset.service.analysis;

public interface SynchronousDataSetAnalyzer extends DataSetAnalyzer {

    /**
     * @return A arbitrary number to order synchronous analysis (one synchronous analysis may depend on other
     * implementation results).
     */
    int order();
}
