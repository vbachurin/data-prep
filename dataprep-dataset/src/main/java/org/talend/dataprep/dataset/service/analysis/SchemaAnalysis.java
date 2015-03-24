package org.talend.dataprep.dataset.service.analysis;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import javax.jms.JMSException;
import javax.jms.Message;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.DataSetContent;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.dataset.service.Destinations;
import org.talend.dataprep.dataset.store.DataSetContentStore;
import org.talend.dataprep.dataset.store.DataSetMetadataRepository;
import org.talend.dataprep.schema.FormatGuess;
import org.talend.dataprep.schema.FormatGuesser;
import org.talend.dataprep.schema.SchemaParser;

@Component
public class SchemaAnalysis {

    private static final Log LOG = LogFactory.getLog(SchemaAnalysis.class);

    @Autowired
    JmsTemplate jmsTemplate;

    @Autowired
    DataSetMetadataRepository repository;

    @Autowired
    DataSetContentStore store;

    @Autowired
    List<FormatGuesser> guessers = new LinkedList<>();

    @JmsListener(destination = Destinations.SCHEMA_ANALYSIS)
    public void analyseDataSetSchema(Message message) {
        try {
            String dataSetId = message.getStringProperty("dataset.id"); //$NON-NLS-1
            DataSetMetadata metadata = repository.get(dataSetId);
            if (metadata != null) {
                // Guess media type based on InputStream
                Set<FormatGuess> mediaTypes = new HashSet<>();
                for (FormatGuesser guesser : guessers) {
                    try (InputStream content = store.getAsRaw(metadata)) {
                        FormatGuess mediaType = guesser.guess(content);
                        mediaTypes.add(mediaType);
                    } catch (IOException e) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Unable to use guesser '" + guesser + "' on data set #" + dataSetId, e);
                        }
                    }
                }
                // Select best format guess
                List<FormatGuess> orderedGuess = new LinkedList<>(mediaTypes);
                Collections.sort(orderedGuess, (g1, g2) -> ((int) (g2.getConfidence() - g1.getConfidence())));
                FormatGuess bestGuess = orderedGuess.get(0);
                DataSetContent dataSetContent = metadata.getContent();
                dataSetContent.setContentType(bestGuess);
                dataSetContent.setContentTypeCandidates(orderedGuess); // Remember format guesses
                repository.add(metadata);
                // Parse information
                try (InputStream content = store.getAsRaw(metadata)) {
                    SchemaParser parser = bestGuess.getSchemaParser();
                    metadata.getRow().setColumns(parser.parse(content));
                    metadata.getLifecycle().schemaAnalyzed(true);
                    repository.add(metadata);
                    // Quality information needs schema information, since it's ready, asks for quality analysis
                    jmsTemplate.send(Destinations.QUALITY_ANALYSIS, session -> {
                        Message qualityAnalysisMessage = session.createMessage();
                        qualityAnalysisMessage.setStringProperty("dataset.id", metadata.getId()); //$NON-NLS-1
                            return qualityAnalysisMessage;
                        });
                } catch (IOException e) {
                    throw new RuntimeException("Unable to read data set content.", e);
                }
            } else {
                LOG.info("Data set #" + dataSetId + " no longer exists.");
            }
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}
