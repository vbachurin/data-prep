package org.talend.dataprep.api.dataset.row;

import java.util.BitSet;
import java.util.List;
import java.util.function.Function;

import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataquality.common.inference.Analyzer;
import org.talend.dataquality.common.inference.Analyzers;
import org.talend.dataquality.common.inference.ValueQualityStatistics;

public class InvalidMarker implements Function<DataSetRow, DataSetRow> {

    private final long[] invalidCount;

    private final Analyzer<Analyzers.Result> analyzer;

    private final List<ColumnMetadata> columns;

    public InvalidMarker(List<ColumnMetadata> columns, Analyzer<Analyzers.Result> analyzer) {
        this.columns = columns;
        this.invalidCount = new long[columns.size()];
        this.analyzer = analyzer;
    }

    @Override
    public DataSetRow apply(DataSetRow dataSetRow) {
        // Find invalid / unknown status for columns
        final String[] values = dataSetRow.filter(columns).order(columns).toArray(DataSetRow.SKIP_TDP_ID);
        BitSet invalidBitSet = new BitSet(values.length);
        final List<Analyzers.Result> columnsAnalysis = analyzer.getResult();
        for (int i = 0; i < columnsAnalysis.size(); i++) {
            final Analyzers.Result columnAnalysis = columnsAnalysis.get(i);
            if (columnAnalysis.exist(ValueQualityStatistics.class)) {
                final long newInvalidCount = columnAnalysis.get(ValueQualityStatistics.class).getInvalidCount();
                if (newInvalidCount > invalidCount[i]) {
                    invalidBitSet.set(i);
                    invalidCount[i] = newInvalidCount;
                }
            }
        }

        // Set invalid / unknown status for columns
        if (!invalidBitSet.isEmpty()) {
            for (int i = 0; i < invalidBitSet.length(); i++) {
                if (invalidBitSet.get(i)) {
                    dataSetRow.setInvalid(columns.get(i).getId());
                }
            }
        }

        return dataSetRow;
    }
}
