package org.talend.dataprep.dataset.objects;

import java.util.ArrayList;
import java.util.List;

public class RowMetadata {

    private List<ColumnMetadata> columnMetadata = new ArrayList<>();

    public RowMetadata(List<ColumnMetadata> columnMetadata) {
        this.columnMetadata = columnMetadata;
    }

    public List<ColumnMetadata> getColumns() {
        return columnMetadata;
    }

}
