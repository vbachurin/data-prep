package org.talend.dataprep.dataset.service.analysis;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.statistics.StatisticsAdapter;
import org.talend.dataprep.dataset.service.Destinations;
import org.talend.dataprep.dataset.store.content.ContentStoreRouter;
import org.talend.dataprep.dataset.store.metadata.DataSetMetadataRepository;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.DataSetErrorCodes;
import org.talend.dataprep.lock.DistributedLock;
import org.talend.dataprep.quality.AnalyzerService;
import org.talend.datascience.common.inference.Analyzer;
import org.talend.datascience.common.inference.Analyzers;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.List;
import java.util.stream.Stream;

import static org.talend.dataprep.exception.error.DataSetErrorCodes.UNABLE_TO_ANALYZE_DATASET_QUALITY;

@Component
public class StatisticsAnalysis implements AsynchronousDataSetAnalyzer {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsAnalysis.class);

    @Autowired
    DataSetMetadataRepository repository;

    @Autowired
    ContentStoreRouter store;

    @Autowired
    StatisticsAdapter adapter;

    @Autowired
    AnalyzerService analyzerService;

    @JmsListener(destination = Destinations.STATISTICS_ANALYSIS)
    public void analyzeQuality(Message message) {
        try {
            String dataSetId = message.getStringProperty("dataset.id"); //$NON-NLS-1$
            try {
                analyze(dataSetId);
            } finally {
                message.acknowledge();
            }
        } catch (JMSException e) {
            throw new TDPException(DataSetErrorCodes.UNEXPECTED_JMS_EXCEPTION, e);
        }
    }

    @Override
    public void analyze(String dataSetId) {
        if (StringUtils.isEmpty(dataSetId)) {
            throw new IllegalArgumentException("Data set id cannot be null or empty.");
        }
        DistributedLock datasetLock = repository.createDatasetMetadataLock(dataSetId);
        datasetLock.lock();
        try {
            DataSetMetadata metadata = repository.get(dataSetId);
            if (metadata != null) {
                if (!metadata.getLifecycle().schemaAnalyzed()) {
                    LOGGER.debug("Dataset {}, schema information must be computed before quality analysis can be performed, ignoring message", metadata.getId());
                    return; // no acknowledge to allow re-poll.
                }

                final List<ColumnMetadata> columns = metadata.getRowMetadata().getColumns();
                if (columns.isEmpty()) {
                    LOGGER.debug("Skip statistics of {} (no column information).", metadata.getId());
                }
                else {
                    try (final Stream<DataSetRow> stream = store.stream(metadata)) {
                        final Analyzer<Analyzers.Result> analyzer = analyzerService.baseAnalysis(columns);
                        computeStatistics(analyzer, columns, stream);
                    } catch (Exception e) {
                        LOGGER.warn("Base statistics analysis, dataset {} generates an error", dataSetId, e);
                        throw new TDPException(UNABLE_TO_ANALYZE_DATASET_QUALITY, e);
                    }

                    try (final Stream<DataSetRow> stream = store.stream(metadata)) {
                        final Analyzer<Analyzers.Result> analyzer = analyzerService.advancedAnalysis(columns);
                        computeStatistics(analyzer, columns, stream);
                    } catch (Exception e) {
                        LOGGER.warn("Advances statistics analysis, dataset {} generates an error", dataSetId, e);
                        throw new TDPException(UNABLE_TO_ANALYZE_DATASET_QUALITY, e);
                    }
                }

                // Tag data set quality: now analyzed
                metadata.getLifecycle().qualityAnalyzed(true);
                repository.add(metadata);
            } else {
                LOGGER.info("Unable to analyze quality of data set #{}: seems to be removed.", dataSetId);
            }
        } finally {
            datasetLock.unlock();
        }

    }

    /**
     * Compute the statistics for the given dataset metadata and content.
     *
     * @param analyzer the analyzer to perform.
     * @param columns the columns metadata.
     * @param stream the content to compute the statistics from.
     */
    private void computeStatistics(final Analyzer<Analyzers.Result> analyzer, final List<ColumnMetadata> columns, final Stream<DataSetRow> stream) {
        // Create a content with the expected format for the StatisticsClientJson class
        stream.map(row -> row.toArray(DataSetRow.SKIP_TDP_ID)).forEach(analyzer::analyze);
        analyzer.end();

        // Store results back in data set
        adapter.adapt(columns, analyzer.getResult());
    }

    /**
     * Compute the statistics for the given dataset metadata and content.
     *
     * @param metadata the metadata to compute the statistics for.
     * @param stream the content to compute the statistics from.
     */
    public void computeFullStatistics(final DataSetMetadata metadata, final Stream<DataSetRow> stream) {
        final List<ColumnMetadata> columns = metadata.getRowMetadata().getColumns();
        if (columns.isEmpty()) {
            LOGGER.debug("Skip statistics of {} (no column information).", metadata.getId());
            return;
        }
        final Analyzer<Analyzers.Result> analyzer = analyzerService.full(columns);
        computeStatistics(analyzer, columns, stream);
    }


    /**
     * @see AsynchronousDataSetAnalyzer#destination()
     */
    @Override
    public String destination() {
        return Destinations.STATISTICS_ANALYSIS;
    }
}
