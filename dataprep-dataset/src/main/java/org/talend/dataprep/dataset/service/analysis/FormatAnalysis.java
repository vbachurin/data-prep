package org.talend.dataprep.dataset.service.analysis;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.jms.JMSException;
import javax.jms.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.talend.dataprep.DistributedLock;
import org.talend.dataprep.api.dataset.DataSetContent;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.dataset.exception.DataSetMessages;
import org.talend.dataprep.dataset.service.Destinations;
import org.talend.dataprep.dataset.store.DataSetContentStore;
import org.talend.dataprep.dataset.store.DataSetMetadataRepository;
import org.talend.dataprep.exception.Exceptions;
import org.talend.dataprep.schema.FormatGuess;
import org.talend.dataprep.schema.FormatGuesser;
import org.talend.dataprep.schema.SchemaParser;
import org.talend.dataprep.schema.SchemaParserResult;

/**
 * Analyzes the raw content of a dataset and determine the best format (XLS, CSV...) for the data set raw content. It
 * also parses column name information. Once analyzed, data prep would know how to access content.
 * 
 * @see DataSetContentStore#get(DataSetMetadata)
 */
@Component
public class FormatAnalysis {

    private static final Logger LOG = LoggerFactory.getLogger(FormatAnalysis.class);

    @Autowired
    ApplicationContext context;

    @Autowired
    JmsTemplate jmsTemplate;

    @Autowired
    DataSetMetadataRepository repository;

    @Autowired
    DataSetContentStore store;

    @Autowired
    List<FormatGuesser> guessers = new LinkedList<>();

    @JmsListener(destination = Destinations.FORMAT_ANALYSIS)
    public void analyseDataSetSchema(Message message) {
        try {
            String dataSetId = message.getStringProperty("dataset.id"); //$NON-NLS-1
            DistributedLock datasetLock = repository.createDatasetMetadataLock(dataSetId);
            datasetLock.lock();
            try {
                DataSetMetadata metadata = repository.get(dataSetId);
                if (metadata != null) {
                    // Guess media type based on InputStream
                    Set<FormatGuesser.Result> mediaTypes = new HashSet<>();
                    for (FormatGuesser guesser : guessers) {
                        try (InputStream content = store.getAsRaw(metadata)) {
                            FormatGuesser.Result mediaType = guesser.guess(content);
                            mediaTypes.add(mediaType);
                        } catch (IOException e) {
                            LOG.debug("Unable to use guesser '" + guesser + "' on data set #" + dataSetId, e);
                        }
                    }
                    // Select best format guess
                    List<FormatGuesser.Result> orderedGuess = new LinkedList<>(mediaTypes);
                    Collections.sort(orderedGuess, (g1, g2) -> ((int) (g2.getFormatGuess().getConfidence() - g1.getFormatGuess()
                            .getConfidence())));

                    FormatGuesser.Result bestGuessResult = orderedGuess.get(0);
                    FormatGuess bestGuess = bestGuessResult.getFormatGuess();
                    DataSetContent dataSetContent = metadata.getContent();
                    dataSetContent.setParameters(bestGuessResult.getParameters());
                    dataSetContent.setFormatGuessId(bestGuess.getBeanId());
                    dataSetContent.setContentTypeCandidates(orderedGuess); // Remember format guesses
                    // Parse column name information
                    try (InputStream content = store.getAsRaw(metadata)) {
                        SchemaParser parser = bestGuess.getSchemaParser();

                        SchemaParserResult schemaParserResult = parser.parse(content, metadata);
                        if (schemaParserResult.draft()) {
                            metadata.setSheetNumber(-1);
                            metadata.setDraft(true);
                            metadata.setSchemaParserResult(schemaParserResult);
                            return;
                        }
                        metadata.setDraft(false);
                        metadata.getRow().setColumns(schemaParserResult.getColumnMetadatas());

                    } catch (IOException e) {
                        throw Exceptions.Internal(DataSetMessages.UNABLE_TO_READ_DATASET_CONTENT, e);
                    }
                    repository.add(metadata);
                    // Asks for a in depth schema analysis (for column type information).
                    jmsTemplate.send(Destinations.SCHEMA_ANALYSIS, session -> {
                        Message schemaAnalysisMessage = session.createMessage();
                        schemaAnalysisMessage.setStringProperty("dataset.id", dataSetId); //$NON-NLS-1
                            return schemaAnalysisMessage;
                        });
                } else {
                    LOG.info("Data set #{} no longer exists.", dataSetId);
                }
            } finally {
                datasetLock.unlock();
                message.acknowledge();
            }
        } catch (JMSException e) {
            throw Exceptions.Internal(DataSetMessages.UNEXPECTED_JMS_EXCEPTION, e);
        }

    }
}
