package org.talend.dataprep.transformation.pipeline.node;

import java.util.function.Predicate;

import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.api.dataset.row.DataSetRow;

public class FilterNode extends BasicNode {

    private final Predicate<DataSetRow> filter;

    public FilterNode(Predicate<DataSetRow> filter) {
        this.filter = filter;
    }

    @Override
    public void receive(DataSetRow row, RowMetadata metadata) {
        if (filter.test(row)) {
            super.receive(row, metadata);
        }
    }

}
