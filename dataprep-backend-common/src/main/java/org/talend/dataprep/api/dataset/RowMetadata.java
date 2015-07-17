package org.talend.dataprep.api.dataset;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;
import org.talend.dataprep.api.dataset.diff.Flag;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Models metadata information for a row of a data set.
 */
public class RowMetadata {

    /** List of row metadata. */
    @JsonProperty("ColumnMetadata")
    private List<ColumnMetadata> columns = new ArrayList<>();

    public RowMetadata() {
        System.out.println("");
    }

    /**
     * Default constructor.
     * 
     * @param columns the list of column metadata.
     */
    public RowMetadata(List<ColumnMetadata> columns) {
        setColumns(columns);
    }

    /**
     * @return The metadata of this row's columns.
     */
    public List<ColumnMetadata> getColumns() {
        return columns;
    }

    /**
     * @param columnMetadata the metadata to set.
     */
    public void setColumns(List<ColumnMetadata> columnMetadata) {
        columns.clear();
        columnMetadata.forEach(this::addColumn);
    }

    private ColumnMetadata addColumn(ColumnMetadata columnMetadata) {
        return addColumn(columnMetadata, columns.size());
    }

    private ColumnMetadata addColumn(ColumnMetadata columnMetadata, int index) {
        DecimalFormat format = new DecimalFormat("0000"); //$NON-NLS-1$
        if (StringUtils.isEmpty(columnMetadata.getId())) {
            columnMetadata.setId(format.format(this.columns.size()));
        }
        columns.add(index, columnMetadata);
        return columnMetadata;
    }


    /**
     * @return the row size.
     */
    public int size() {
        return columns.size();
    }

    /**
     * @param wantedId the wanted column id.
     * @return return the wanted columnMetadata or null if not found.
     */
    public ColumnMetadata getById(String wantedId) {
        // defensive programming
        if (wantedId == null) {
            return null;
        }
        for (ColumnMetadata column : columns) {
            if (wantedId.equals(column.getId())) {
                return column;
            }
        }
        return null;
    }

    /**
     * Compute the diff from the given reference to this and update the diffFlag on each columnMetadata.
     * 
     * @param reference the starting point to compute the diff.
     */
    public void diff(RowMetadata reference) {

        // process the new columns
        columns.forEach(column -> {
            if (reference.getById(column.getId()) == null) {
                column.setDiffFlagValue(Flag.NEW.getValue());
            }
        });

        // process the updated columns
        columns.forEach(column -> {
            ColumnMetadata referenceColumn = reference.getById(column.getId());
            if (referenceColumn != null && !column.getName().equals(referenceColumn.getName())) {
                column.setDiffFlagValue(Flag.UPDATE.getValue());
            }
        });

        // process the deleted columns (add the deleted ones)
        reference.getColumns().forEach(referenceColumn -> {
            if (getById(referenceColumn.getId()) == null) {
                referenceColumn.setDiffFlagValue(Flag.DELETE.getValue());
                columns.add(referenceColumn);
            }
        });

        // sort the columns so that the deleted ones get placed where the were
        columns.sort((col1, col2) -> col1.getId().compareTo(col2.getId()));
    }

    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return "RowMetadata{" + "columns=" + columns + '}';
    }

    /**
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RowMetadata that = (RowMetadata) o;
        return Objects.equals(columns, that.columns);
    }

    /**
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(columns);
    }

    /**
     * Insert a new column in this metadata right after the existing <code>columnId</code>. If no column with
     * <code>columnId</code> is to be found, append new column at the end of this row's columns.
     * 
     * @param columnId A non null column id. Empty string is allowed.
     * @param column A non null column to insert in this row's metadata.
     * @return The column id of the newly inserted column.
     */
    public String insertAfter(@Nonnull String columnId, @Nonnull ColumnMetadata column) {
        int insertIndex = 0;
        for (ColumnMetadata columnMetadata : columns) {
            insertIndex++;
            if (columnId.equals(columnMetadata.getId())) {
                break;
            }
        }
        addColumn(column, insertIndex);
        return column.getId();
    }

    public RowMetadata clone() {
        return new RowMetadata(new ArrayList<>(columns));
    }
}
