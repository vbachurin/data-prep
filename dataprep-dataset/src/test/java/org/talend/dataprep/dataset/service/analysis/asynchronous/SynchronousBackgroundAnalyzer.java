package org.talend.dataprep.dataset.service.analysis.asynchronous;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.statistics.StatisticsAdapter;
import org.talend.dataprep.dataset.service.analysis.synchronous.SynchronousDataSetAnalyzer;
import org.talend.dataprep.dataset.store.content.ContentStoreRouter;
import org.talend.dataprep.dataset.store.metadata.DataSetMetadataRepository;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.DataSetErrorCodes;
import org.talend.dataprep.quality.AnalyzerService;
import org.talend.dataquality.common.inference.Analyzer;
import org.talend.dataquality.common.inference.Analyzers;

import java.util.List;
import java.util.stream.Stream;

@Component
public class SynchronousBackgroundAnalyzer implements SynchronousDataSetAnalyzer {

    @Autowired
    AnalyzerService analyzerService;

    @Autowired
    StatisticsAdapter adapter;

    @Autowired
    ContentStoreRouter store;

    @Autowired
    DataSetMetadataRepository repository;

    @Override
    public int order() {
        return 4;
    }

    @Override
    public void analyze(String dataSetId) {
        if (StringUtils.isEmpty(dataSetId)) {
            throw new IllegalArgumentException();
        }
        final DataSetMetadata metadata = repository.get(dataSetId);
        if (metadata == null) {
            return;
        }
        final List<ColumnMetadata> columns = metadata.getRowMetadata().getColumns();
        try (Stream<DataSetRow> stream = store.stream(metadata)) {
            try (Analyzer<Analyzers.Result> analyzer = analyzerService.baselineAnalysis(columns)) {
                stream.map(row -> row.toArray(DataSetRow.SKIP_TDP_ID)).forEach(analyzer::analyze);
                analyzer.end();
                // Store results back in data set
                adapter.adapt(columns, analyzer.getResult());
            } catch (Exception e) {
                throw new TDPException(DataSetErrorCodes.UNABLE_TO_ANALYZE_DATASET_QUALITY, e);
            }
        }
        try (Stream<DataSetRow> stream = store.stream(metadata)) {
            try (Analyzer<Analyzers.Result> analyzer = analyzerService.full(columns)) {
                stream.map(row -> row.toArray(DataSetRow.SKIP_TDP_ID)).forEach(analyzer::analyze);
                analyzer.end();
                // Store results back in data set
                adapter.adapt(columns, analyzer.getResult());
            } catch (Exception e) {
                throw new TDPException(DataSetErrorCodes.UNABLE_TO_ANALYZE_DATASET_QUALITY, e);
            }
        }
    }
}
