package org.talend.dataprep.dataset.service.analysis.asynchronous;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.talend.dataprep.dataset.service.analysis.synchronous.SynchronousDataSetAnalyzer;

@Component
@ConditionalOnProperty(name = "dataset.asynchronous.analysis", havingValue = "false")
public class SyncBackgroundAnalyzer implements SynchronousDataSetAnalyzer {

    @Autowired
    BackgroundAnalysis backgroundAnalysis;

    @Override
    public int order() {
        return 4;
    }

    @Override
    public void analyze(String dataSetId) {
        backgroundAnalysis.analyze(dataSetId);
    }
}
