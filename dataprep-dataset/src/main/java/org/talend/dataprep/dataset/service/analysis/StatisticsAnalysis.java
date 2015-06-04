package org.talend.dataprep.dataset.service.analysis;

import java.io.StringWriter;
import java.util.Iterator;
import java.util.stream.Stream;

import javax.jms.JMSException;
import javax.jms.Message;

import org.apache.commons.lang.StringUtils;
import org.apache.spark.SparkContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.talend.dataprep.DistributedLock;
import org.talend.dataprep.api.dataset.*;
import org.talend.dataprep.dataset.exception.DataSetErrorCodes;
import org.talend.dataprep.dataset.service.Destinations;
import org.talend.dataprep.dataset.store.DataSetContentStore;
import org.talend.dataprep.dataset.store.DataSetMetadataRepository;
import org.talend.dataprep.exception.TDPException;
import org.talend.datascience.statistics.StatisticsClientJson;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;

@Component
@ConditionalOnProperty("dataset.spark.master")
public class StatisticsAnalysis {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsAnalysis.class);

    @Autowired
    DataSetMetadataRepository repository;

    @Autowired
    DataSetContentStore store;

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    SparkContext sparkContext;

    @Autowired
    Jackson2ObjectMapperBuilder builder;

    @JmsListener(destination = Destinations.STATISTICS_ANALYSIS)
    public void analyzeQuality(Message message) {
        try {
            String dataSetId = message.getStringProperty("dataset.id"); //$NON-NLS-1$
            DistributedLock datasetLock = repository.createDatasetMetadataLock(dataSetId);
            datasetLock.lock();
            try {
                DataSetMetadata metadata = repository.get(dataSetId);
                try (Stream<DataSetRow> stream = store.stream(metadata)) {
                    StatisticsClientJson statisticsClient = new StatisticsClientJson(true, sparkContext);
                    statisticsClient.setJsonRecordPath("records"); //$NON-NLS-1$
                    if (metadata != null) {
                        if (!metadata.getLifecycle().schemaAnalyzed()) {
                            LOGGER.debug("Schema information must be computed before quality analysis can be performed, ignoring message");
                            return; // no acknowledge to allow re-poll.
                        }
                        // Create a content with the expected format for the StatisticsClientJson class
                        DataSet dataSet = new DataSet();
                        dataSet.setMetadata(metadata);
                        dataSet.setRecords(stream);
                        final StringWriter content = new StringWriter();
                        builder.build().writer().writeValue(content, dataSet);
                        // Build schema for the content (JSON format expected by statistics library).
                        StringWriter schema = new StringWriter();
                        JsonGenerator generator = new JsonFactory().createGenerator(schema);
                        generator.writeStartObject();
                        {
                            generator.writeFieldName("column"); //$NON-NLS-1$
                            generator.writeStartArray();
                            {
                                for (ColumnMetadata column : metadata.getRow().getColumns()) {
                                    generator.writeStartObject();
                                    {
                                        generator.writeStringField("name", StringUtils.EMPTY); //$NON-NLS-1$
                                        generator.writeStringField("id", StringUtils.EMPTY); //$NON-NLS-1$
                                        generator.writeStringField("type", column.getType()); //$NON-NLS-1$
                                        generator.writeStringField("suggested type", column.getType()); //$NON-NLS-1$
                                        // Types
                                        generator.writeArrayFieldStart("types"); //$NON-NLS-1$
                                        generator.writeStartObject();
                                        {
                                            generator.writeStringField("name", column.getType()); //$NON-NLS-1$
                                            generator.writeNumberField("occurrences", metadata.getContent().getNbRecords());
                                        }
                                        generator.writeEndObject();
                                        generator.writeEndArray();
                                    }
                                    generator.writeEndObject();
                                }
                            }
                            generator.writeEndArray();
                        }
                        generator.writeEndObject();
                        generator.flush();
                        // Compute statistics
                        int topKfreqTable = 5;
                        String binsOrBuckets = "2"; //$NON-NLS-1$
                        statisticsClient.setSchema(schema.toString());
                        String jsonResult = statisticsClient.doStatisticsInMemory(content.toString(), topKfreqTable,
                                binsOrBuckets);
                        LOGGER.debug("Quality results: {}", jsonResult);
                        // Use result from quality analysis
                        final Iterator<JsonNode> columns = builder.build().readTree(jsonResult).get("column").elements(); //$NON-NLS-1$
                        final Iterator<ColumnMetadata> schemaColumns = metadata.getRow().getColumns().iterator();
                        for (; columns.hasNext() && schemaColumns.hasNext();) {
                            final JsonNode column = columns.next();
                            final ColumnMetadata schemaColumn = schemaColumns.next();
                            // Get the statistics from the returned JSON
                            final JsonNode statistics = column.get("statistics"); //$NON-NLS-1$
                            final int valid = statistics.get("valid").asInt();
                            final int invalid = statistics.get("invalid").asInt();
                            final int empty = statistics.get("empty").asInt();
                            // Set it back to the data prep beans
                            final Quality quality = schemaColumn.getQuality();
                            quality.setValid(valid);
                            quality.setInvalid(invalid);
                            quality.setEmpty(empty);
                            // Keeps the statistics as returned by statistics library.
                            schemaColumn.setStatistics(statistics.toString());
                        }
                        if (columns.hasNext() || schemaColumns.hasNext()) {
                            // Awkward situation: analysis code and parsed content information did not find same number
                            // of columns
                            LOGGER.warn(
                                    "Quality analysis and parsed columns for #{} do not yield same number of columns (content parsed: {} / analysis: {}).",
                                    dataSetId, schemaColumns.hasNext(), columns.hasNext());
                        }
                        metadata.getLifecycle().qualityAnalyzed(true);
                        repository.add(metadata);
                    } else {
                        LOGGER.info("Unable to analyze quality of data set #{}: seems to be removed.", dataSetId);
                    }
                } catch (Exception e) {
                    if (metadata != null) {
                        // just log it
                        LOGGER.warn( "dataset '{}' generate an error, message: {} ", dataSetId, e.getMessage() );
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug( "dataset '"+ dataSetId +"' generate an error", e );
                        }
                        // FIXME there is an error but with the following code the datasets will be still in the list
                        // the user won't be able to read and will get a weird error without any nice explanation
                        metadata.getLifecycle().error(true);
                        repository.add(metadata);
                    }
                    throw new TDPException(DataSetErrorCodes.UNABLE_TO_ANALYZE_DATASET_QUALITY, e);
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
