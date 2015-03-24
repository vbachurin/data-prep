package org.talend.dataprep.api.dataset;

import java.util.ArrayList;
import java.util.List;

/**
 * Represent metadata information for a row of a data set.
 */
public class RowMetadata {

    private List<ColumnMetadata> columnMetadata = new ArrayList<>();

    public RowMetadata(List<ColumnMetadata> columnMetadata) {
        this.columnMetadata = columnMetadata;
    }

    /**
     * @return The metadata of this row's columns.
     */
    public List<ColumnMetadata> getColumns() {
        return columnMetadata;
    }

    public void setColumns(List<ColumnMetadata> columnMetadata) {
        this.columnMetadata = columnMetadata;
    }
}
