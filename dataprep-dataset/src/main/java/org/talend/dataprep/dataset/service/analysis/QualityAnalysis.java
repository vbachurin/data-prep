package org.talend.dataprep.dataset.service.analysis;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.talend.dataprep.dataset.objects.DataSetMetadata;
import org.talend.dataprep.dataset.service.Destinations;
import org.talend.dataprep.dataset.store.DataSetContentStore;
import org.talend.dataprep.dataset.store.DataSetMetadataRepository;

import javax.jms.JMSException;
import javax.jms.Message;
import java.io.IOException;
import java.io.InputStream;

@Component
public class QualityAnalysis {

    @Autowired
    DataSetMetadataRepository repository;

    @Autowired
    DataSetContentStore store;

    @JmsListener(destination = Destinations.QUALITY_ANALYSIS)
    public void indexDataSet(Message message) {
        try {
            String dataSetId = message.getStringProperty("dataset.id"); //$NON-NLS-1
            DataSetMetadata metadata = repository.get(dataSetId);
            if (!metadata.getLifecycle().schemaAnalyzed()) {
                throw new IllegalStateException("Schema information must be computed before quality analysis can be performed.");
            }
            try (InputStream content = store.getAsRaw(metadata)) {
                IOUtils.toString(content); // Consumes raw content
                metadata.getLifecycle().qualityAnalyzed(true);
                repository.add(metadata);
            } catch (IOException e) {
                throw new RuntimeException("Unable to read data set content.");
            }
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}
