package org.talend.dataprep.api.dataset;

import java.util.ArrayList;
import java.util.List;

import org.talend.dataprep.api.dataset.diff.Flag;

/**
 * Models metadata information for a row of a data set.
 */
public class RowMetadata {

    /** List of row metadata. */
    private List<ColumnMetadata> columnMetadatas = new ArrayList<>();

    /**
     * Default constructor.
     * 
     * @param columnMetadata the list of column metadata.
     */
    public RowMetadata(List<ColumnMetadata> columnMetadata) {
        this.columnMetadatas = columnMetadata;
    }

    /**
     * @return The metadata of this row's columns.
     */
    public List<ColumnMetadata> getColumns() {
        return columnMetadatas;
    }

    /**
     * @param columnMetadata the metadata to set.
     */
    public void setColumns(List<ColumnMetadata> columnMetadata) {
        this.columnMetadatas = columnMetadata;
    }

    /**
     * @return the row size.
     */
    public int size() {
        return columnMetadatas.size();
    }

    /**
     * @param wantedId the wanted column id.
     * @return return the wanted columnMetadata or null if not found.
     */
    protected ColumnMetadata getById(String wantedId) {
        // defensive programming
        if (wantedId == null) {
            return null;
        }
        for (ColumnMetadata column : columnMetadatas) {
            if (wantedId.equals(column.getId())) {
                return column;
            }
        }
        return null;
    }

    /**
     * Compute the diff from the given reference to this and update the diffFlag on each columnMetadatas.
     * 
     * @param reference the starting point to compute the diff.
     */
    public void diff(RowMetadata reference) {

        // process the new columns
        columnMetadatas.forEach(column -> {
            if (reference.getById(column.getId()) == null) {
                column.setDiffFlagValue(Flag.NEW.getValue());
            }
        });

        // process the updated columns
        columnMetadatas.forEach(column -> {
            ColumnMetadata referenceColumn = reference.getById(column.getId());
            if (referenceColumn != null) {
                if (!column.getName().equals(referenceColumn.getName())) {
                    column.setDiffFlagValue(Flag.UPDATE.getValue());
                }
            }
        });

        // process the deleted columns (add the deleted ones)
        reference.getColumns().forEach(referenceColumn -> {
            if (getById(referenceColumn.getId()) == null) {
                referenceColumn.setDiffFlagValue(Flag.DELETE.getValue());
                columnMetadatas.add(referenceColumn);
            }
        });

        // sort the columns so that the deleted ones get placed where the were
        columnMetadatas.sort((col1, col2) -> col1.getId().compareTo(col2.getId()));
    }

    @Override
    public String toString()
    {
        return "RowMetadata{" +
            "columnMetadata=" + columnMetadata +
            ", previousMetadata=" + previousMetadata +
            '}';
    }
}
