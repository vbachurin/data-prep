package org.talend.dataprep.dataset.service.analysis;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.ColumnMetadata;
import org.talend.dataprep.api.DataSetMetadata;
import org.talend.dataprep.api.Quality;
import org.talend.dataprep.dataset.service.Destinations;
import org.talend.dataprep.dataset.store.DataSetContentStore;
import org.talend.dataprep.dataset.store.DataSetMetadataRepository;

import javax.jms.JMSException;
import javax.jms.Message;

@Component
public class QualityAnalysis {

    public static final Log LOGGER = LogFactory.getLog(QualityAnalysis.class);

    @Autowired
    DataSetMetadataRepository repository;

    @Autowired
    DataSetContentStore store;

    @JmsListener(destination = Destinations.QUALITY_ANALYSIS)
    public void indexDataSet(Message message) {
        try {
            String dataSetId = message.getStringProperty("dataset.id"); //$NON-NLS-1
            DataSetMetadata metadata = repository.get(dataSetId);
            if (metadata != null) {
                if (!metadata.getLifecycle().schemaAnalyzed()) {
                    throw new IllegalStateException(
                            "Schema information must be computed before quality analysis can be performed.");
                }
                for (ColumnMetadata column : metadata.getRow().getColumns()) {
                    Quality quality = column.getQuality();
                    quality.setEmpty(5);
                    quality.setInvalid(10);
                    quality.setValid(72);
                }
                metadata.getLifecycle().qualityAnalyzed(true);
                repository.add(metadata);
            } else {
                LOGGER.info("Unable to analyze quality of data set #" + dataSetId + ": seems to be removed.");
            }
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}
