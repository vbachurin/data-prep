package org.talend.dataprep.api.dataset;

import java.util.ArrayList;
import java.util.List;

/**
 * Models metadata information for a row of a data set.
 */
public class RowMetadata {

    /** List of row metadata. */
    private List<ColumnMetadata> columnMetadata = new ArrayList<>();

    public RowMetadata() {
    }

    /** The previous metadata in case of a diff. */
    private List<ColumnMetadata> previousMetadata;

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

    /**
     * Set the previous row metadata and compute the diff with the current one.
     * 
     * @param previousMetadata to set.
     */
    public void setPreviousMetadata(RowMetadata previousMetadata) {
        this.previousMetadata = previousMetadata.getColumns();
        updateDiffMetadata();
    }

    /**
     * Update the internal diff metadata.
     */
    private void updateDiffMetadata() {

        // quick test to prevent cpu waste
        if (previousMetadata == null) {
            return;
        }

    }

}
