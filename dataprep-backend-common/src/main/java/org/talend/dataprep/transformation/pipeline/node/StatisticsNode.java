package org.talend.dataprep.transformation.pipeline.node;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.api.dataset.row.DataSetRow;
import org.talend.dataprep.api.dataset.statistics.StatisticsAdapter;
import org.talend.dataprep.transformation.pipeline.Signal;
import org.talend.dataquality.common.inference.Analyzer;
import org.talend.dataquality.common.inference.Analyzers;

import static java.util.stream.Collectors.toList;

public class StatisticsNode extends ColumnFilteredNode {

    private final Function<List<ColumnMetadata>, Analyzer<Analyzers.Result>> analyzer;

    private final StatisticsAdapter adapter;

    private Analyzer<Analyzers.Result> configuredAnalyzer;

    public StatisticsNode(Function<List<ColumnMetadata>, Analyzer<Analyzers.Result>> analyzer, Predicate<ColumnMetadata> filter, StatisticsAdapter adapter) {
        super(filter);
        this.analyzer = analyzer;
        this.adapter = adapter;
    }

    @Override
    public void receive(DataSetRow row, RowMetadata metadata) {
        performColumnFilter(row, metadata);
        if (configuredAnalyzer == null) {
            this.configuredAnalyzer = analyzer.apply(filteredColumns);
        }
        configuredAnalyzer.analyze(row.filter(filteredColumns).order(filteredColumns).toArray(DataSetRow.SKIP_TDP_ID));
        super.receive(row, metadata);
    }

    @Override
    public void signal(Signal signal) {
        adapter.adapt(filteredColumns, configuredAnalyzer.getResult());
        super.signal(signal);
    }
}
