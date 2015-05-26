package org.talend.dataprep.api.dataset;

import java.util.ArrayList;
import java.util.List;

/**
 * Models metadata information for a row of a data set.
 */
public class RowMetadata {

    /** List of row metadata. */
    private List<ColumnMetadata> columnMetadata = new ArrayList<>();

    /**
     * Default constructor.
     * 
     * @param columnMetadata the list of column metadata.
     */
    public RowMetadata(List<ColumnMetadata> columnMetadata) {
        this.columnMetadata = columnMetadata;
    }

    /**
     * @return The metadata of this row's columns.
     */
    public List<ColumnMetadata> getColumns() {
        return columnMetadata;
    }

    /**
     * @param columnMetadata the metadata to set.
     */
    public void setColumns(List<ColumnMetadata> columnMetadata) {
        this.columnMetadata = columnMetadata;
    }

    /**
     * @return the row size.
     */
    public int size() {
        return columnMetadata.size();
    }

}
