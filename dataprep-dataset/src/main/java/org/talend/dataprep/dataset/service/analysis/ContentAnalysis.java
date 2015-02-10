package org.talend.dataprep.dataset.service.analysis;

import java.io.IOException;
import java.io.InputStream;

import javax.jms.JMSException;
import javax.jms.Message;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.DataSetMetadata;
import org.talend.dataprep.dataset.service.Destinations;
import org.talend.dataprep.dataset.store.DataSetContentStore;
import org.talend.dataprep.dataset.store.DataSetMetadataRepository;

@Component
public class ContentAnalysis {

    private static final Log LOG = LogFactory.getLog(ContentAnalysis.class);

    @Autowired
    DataSetMetadataRepository repository;

    @Autowired
    DataSetContentStore store;

    @JmsListener(destination = Destinations.CONTENT_ANALYSIS)
    public void indexDataSet(Message message) {
        try {
            String dataSetId = message.getStringProperty("dataset.id"); //$NON-NLS-1
            DataSetMetadata metadata = repository.get(dataSetId);
            if (metadata != null) {
                try (InputStream content = store.getAsRaw(metadata)) {
                    IOUtils.toString(content); // Consumes raw content
                    metadata.getLifecycle().contentIndexed(true);
                    repository.add(metadata);
                } catch (IOException e) {
                    throw new RuntimeException("Unable to read data set content.");
                }
            } else {
                LOG.info("Data set #" + dataSetId + " no longer exists.");
            }
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}
