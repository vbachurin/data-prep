package org.talend.dataprep.dataset.service;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.talend.dataprep.dataset.objects.DataSetMetadata;
import org.talend.dataprep.dataset.store.DataSetContentStore;
import org.talend.dataprep.dataset.store.DataSetMetadataRepository;

import javax.jms.JMSException;
import javax.jms.Message;
import java.io.IOException;
import java.io.InputStream;

@Component
public class ContentAnalysis {

    @Autowired
    DataSetMetadataRepository repository;

    @Autowired
    DataSetContentStore store;

    @JmsListener(destination = Destinations.INDEXING)
    public void indexDataSet(Message message) {
        try {
            String dataSetId = message.getStringProperty("dataset.id"); //$NON-NLS-1
            DataSetMetadata metadata = repository.get(dataSetId);
            try (InputStream content = store.getAsRaw(metadata)) {
                IOUtils.toString(content); // Consumes raw content
                metadata.getLifecycle().contentIndexed(true);
                repository.add(metadata);
            } catch (IOException e) {
                throw new RuntimeException("Unable to read data set content.");
            }
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}
