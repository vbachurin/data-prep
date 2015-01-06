package org.talend.dataprep.dataset.service.analysis;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
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
public class SchemaAnalysis {

    private static final Log  LOG = LogFactory.getLog(SchemaAnalysis.class);

    @Autowired
    JmsTemplate               jmsTemplate;

    @Autowired
    DataSetMetadataRepository repository;

    @Autowired
    DataSetContentStore       store;

    @JmsListener(destination = Destinations.SCHEMA_ANALYSIS)
    public void analyseDataSetSchema(Message message) {
        try {
            String dataSetId = message.getStringProperty("dataset.id"); //$NON-NLS-1
            DataSetMetadata metadata = repository.get(dataSetId);
            if (metadata != null) {
                try (InputStream content = store.getAsRaw(metadata)) {
                    IOUtils.toString(content); // Consumes raw content
                    metadata.getLifecycle().schemaAnalyzed(true);
                    repository.add(metadata);
                    // Quality information needs schema information, since it's ready, asks for quality analysis
                    jmsTemplate.send(Destinations.QUALITY_ANALYSIS, session -> {
                        Message qualityAnalysisMessage = session.createMessage();
                        qualityAnalysisMessage.setStringProperty("dataset.id", metadata.getId()); //$NON-NLS-1
                            return qualityAnalysisMessage;
                        });
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
