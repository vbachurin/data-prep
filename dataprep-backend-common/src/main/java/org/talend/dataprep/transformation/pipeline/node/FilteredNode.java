// ============================================================================
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// https://github.com/Talend/data-prep/blob/master/LICENSE
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package org.talend.dataprep.transformation.pipeline.node;

import java.util.function.Function;
import java.util.function.Predicate;

import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.api.dataset.row.DataSetRow;
import org.talend.dataprep.transformation.pipeline.Node;

public class FilteredNode extends BasicNode {

    private final Function<RowMetadata, Predicate<DataSetRow>> filter;

    private transient Predicate<DataSetRow> instance;

    public FilteredNode(Function<RowMetadata, Predicate<DataSetRow>> filter) {
        this.filter = filter;
    }

    @Override
    public void receive(DataSetRow row, RowMetadata metadata) {
        synchronized (filter) {
            if (instance == null) {
                instance = filter.apply(metadata);
            }
        }
        if (instance.test(row)) {
            super.receive(row, metadata);
        }
    }

    @Override
    public Node copyShallow() {
        return new FilteredNode(filter);
    }
}
