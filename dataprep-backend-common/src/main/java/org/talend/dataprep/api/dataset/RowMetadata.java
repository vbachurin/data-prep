package org.talend.dataprep.api.dataset;

import java.util.ArrayList;
import java.util.List;

import org.talend.dataprep.api.dataset.diff.Flag;

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

    /**
     * Compute the diff from the given reference to this and update the diffFlag on each columnMetadata.
     * 
     * @param reference the starting point to compute the diff.
     */
    public void diff(RowMetadata reference) {

        List<ColumnMetadata> referenceColumns = reference.getColumns();

        // process the new columns
        columnMetadata.forEach(column -> {
            if (!referenceColumns.contains(column)) {
                column.setDiffFlagValue(Flag.NEW.getValue());
            }
        });

        // process the deleted columns (add the deleted ones)
        referenceColumns.forEach(column -> {
            if (!columnMetadata.contains(column)) {
                column.setDiffFlagValue(Flag.DELETE.getValue());
                columnMetadata.add(column);
            }
        });

    }

}
