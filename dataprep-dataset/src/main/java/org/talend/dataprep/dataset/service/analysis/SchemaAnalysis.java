package org.talend.dataprep.dataset.service.analysis;

import java.net.URI;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.statistics.StatisticsAdapter;
import org.talend.dataprep.api.type.TypeUtils;
import org.talend.dataprep.dataset.store.content.ContentStoreRouter;
import org.talend.dataprep.dataset.store.metadata.DataSetMetadataRepository;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.DataSetErrorCodes;
import org.talend.dataprep.lock.DistributedLock;
import org.talend.dataquality.semantic.recognizer.CategoryRecognizerBuilder;
import org.talend.dataquality.semantic.statistics.SemanticAnalyzer;
import org.talend.dataquality.statistics.quality.ValueQualityAnalyzer;
import org.talend.datascience.common.inference.Analyzer;
import org.talend.datascience.common.inference.Analyzers;
import org.talend.datascience.common.inference.type.DataTypeAnalyzer;

@Component
public class SchemaAnalysis implements SynchronousDataSetAnalyzer {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchemaAnalysis.class);

    @Autowired
    DataSetMetadataRepository repository;

    @Autowired
    ContentStoreRouter store;

    @Autowired
    StatisticsAdapter adapter;

    @Override
    public void analyze(String dataSetId) {
        if (StringUtils.isEmpty(dataSetId)) {
            throw new IllegalArgumentException("Data set id cannot be null or empty.");
        }
        DistributedLock datasetLock = repository.createDatasetMetadataLock(dataSetId);
        datasetLock.lock();
        try {
            DataSetMetadata metadata = repository.get(dataSetId);
            if (metadata == null) {
                LOGGER.info("Unable to analyze schema of data set #{}: seems to be removed.", dataSetId);
                return;
            }
            // Schema analysis
            try (Stream<DataSetRow> stream = store.stream(metadata)) {
                LOGGER.info("Analyzing schema in dataset #{}...", dataSetId);
                // Configure analyzers
                final URI ddPath = this.getClass().getResource("/luceneIdx/dictionary").toURI(); //$NON-NLS-1$
                final URI kwPath = this.getClass().getResource("/luceneIdx/keyword").toURI(); //$NON-NLS-1$
                final CategoryRecognizerBuilder builder = CategoryRecognizerBuilder.newBuilder() //
                        .ddPath(ddPath) //
                        .kwPath(kwPath) //
                        .setMode(CategoryRecognizerBuilder.Mode.LUCENE);
                final DataTypeAnalyzer dataTypeAnalyzer = new DataTypeAnalyzer();
                final SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(builder);
                final Analyzer<Analyzers.Result> analyzer = Analyzers.with(dataTypeAnalyzer, //
                        semanticAnalyzer, //
                        new ValueQualityAnalyzer(TypeUtils.convert(metadata.getRow().getColumns())) //
                );
                // Determine schema for the content (on the 20 first rows).
                stream.limit(20).map(row -> row.toArray(DataSetRow.SKIP_TDP_ID)).forEach(analyzer::analyze);
                // Find the best suitable type
                adapter.adapt(metadata.getRow().getColumns(), analyzer.getResult());
                LOGGER.info("Analyzed schema in dataset #{}.", dataSetId);
                metadata.getLifecycle().schemaAnalyzed(true);
                repository.add(metadata);
            } catch (Exception e) {
                LOGGER.error("Unable to analyse schema for dataset " + dataSetId + ".", e);
                throw new TDPException(DataSetErrorCodes.UNABLE_TO_ANALYZE_COLUMN_TYPES, e);
            }
        } finally {
            datasetLock.unlock();
        }
    }

    @Override
    public int order() {
        return 1;
    }
}