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

public class StatisticsNode extends ColumnFilteredNode {

    private final Function<List<ColumnMetadata>, Analyzer<Analyzers.Result>> delayedAnalyzer;

    private final StatisticsAdapter adapter;

    private Analyzer<Analyzers.Result> configuredAnalyzer;

    public StatisticsNode(Function<List<ColumnMetadata>, Analyzer<Analyzers.Result>> delayedAnalyzer, Predicate<ColumnMetadata> filter, StatisticsAdapter adapter) {
        super(filter);
        this.delayedAnalyzer = delayedAnalyzer;
        this.adapter = adapter;
    }

    @Override
    public void receive(DataSetRow row, RowMetadata metadata) {
        performColumnFilter(row, metadata);
        if (configuredAnalyzer == null) {
            this.configuredAnalyzer = delayedAnalyzer.apply(metadata.getColumns());
        }
        configuredAnalyzer.analyze(row.filter(metadata.getColumns()).order(metadata.getColumns()).toArray(DataSetRow.SKIP_TDP_ID));
        super.receive(row, metadata);
    }

    @Override
    public void signal(Signal signal) {
        adapter.adapt(rowMetadata.getColumns(), configuredAnalyzer.getResult());
        super.signal(signal);
    }
}
