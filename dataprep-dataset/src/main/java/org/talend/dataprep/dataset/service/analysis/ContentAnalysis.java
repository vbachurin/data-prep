package org.talend.dataprep.dataset.service.analysis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.jms.JMSException;
import javax.jms.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.talend.dataprep.DistributedLock;
import org.talend.dataprep.api.dataset.DataSetContent;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.dataset.exception.DataSetErrorCodes;
import org.talend.dataprep.dataset.service.Destinations;
import org.talend.dataprep.dataset.store.DataSetContentStore;
import org.talend.dataprep.dataset.store.DataSetMetadataRepository;
import org.talend.dataprep.exception.TDPException;

@Component
public class ContentAnalysis {

    private static final Logger LOG = LoggerFactory.getLogger(ContentAnalysis.class);

    @Autowired
    ApplicationContext appContext;

    @Autowired
    DataSetMetadataRepository repository;

    @Autowired
    DataSetContentStore store;

    @JmsListener(destination = Destinations.CONTENT_ANALYSIS)
    public void indexDataSet(Message message) {
        try {
            String dataSetId = message.getStringProperty("dataset.id"); //$NON-NLS-1
            DistributedLock datasetLock = repository.createDatasetMetadataLock(dataSetId);
            datasetLock.lock();
            try {
                DataSetMetadata metadata = repository.get(dataSetId);
                if (metadata != null) {
                    try (BufferedReader content = new BufferedReader(new InputStreamReader(store.getAsRaw(metadata)))) {
                        int lineCount = 0;
                        while (content.readLine() != null) {
                            lineCount++;
                        }
                        DataSetContent datasetContent = metadata.getContent();
                        datasetContent.setNbLinesInHeader(1);
                        datasetContent.setNbLinesInFooter(0);
                        datasetContent.setNbRecords(lineCount - datasetContent.getNbLinesInHeader()
                                - datasetContent.getNbLinesInFooter());

                        metadata.getLifecycle().contentIndexed(true);
                        repository.add(metadata);
                    } catch (IOException e) {
                        throw new TDPException(DataSetErrorCodes.UNABLE_TO_READ_DATASET_CONTENT, e);
                    }
                } else {
                    LOG.info("Data set #{} no longer exists.", dataSetId); //$NON-NLS-1$
                }
            } finally {
                datasetLock.unlock();
                message.acknowledge();
            }
        } catch (JMSException e) {
            throw new TDPException(DataSetErrorCodes.UNEXPECTED_JMS_EXCEPTION, e);
        }
    }
}
