package org.talend.dataprep.dataset.service.analysis;

import java.util.Random;

import javax.jms.JMSException;
import javax.jms.Message;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.api.dataset.Quality;
import org.talend.dataprep.dataset.service.Destinations;
import org.talend.dataprep.dataset.store.DataSetContentStore;
import org.talend.dataprep.dataset.store.DataSetMetadataRepository;

@Component
public class QualityAnalysis {

    private static final Log LOGGER = LogFactory.getLog(QualityAnalysis.class);

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
                    LOGGER.debug("Schema information must be computed before quality analysis can be performed, ignoring message");
                    return; // no acknowledge to allow re-poll.
                }
                for (ColumnMetadata column : metadata.getRow().getColumns()) {
                    Quality quality = column.getQuality();
                    // Computes random quality
                    Random random = new Random();
                    int valid = 50 + random.nextInt(20);
                    int invalid = 25 + random.nextInt(5);
                    int empty = 100 - valid - invalid;
                    quality.setValid(valid);
                    quality.setInvalid(invalid);
                    quality.setEmpty(empty);
                }
                metadata.getLifecycle().qualityAnalyzed(true);
                repository.add(metadata);
                message.acknowledge();
            } else {
                LOGGER.info("Unable to analyze quality of data set #" + dataSetId + ": seems to be removed.");
            }
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}
