package org.talend.dataprep.transformation.actions.common.new_actions_api;

/**
 * Materialize user selection when the actions is called to provide the most relevant behavior.
 * For now there are four possible cases:
 * <ul>
 * <li>The user has no selection and all fields are then empty.</li>
 * <li>The user has selected a row, then only column metadata is null.</li>
 * <li>The user has selected a column and column metadata is not null.</li>
 * <li>The user has selected a cell and then row and column data are here.</li>
 * </ul>
 */
public class UserSelection {

    private final Long selectedRowId;

    private final DatasetRow selectedRow;

    private final ColumnMetadata columnMetadata;

    public UserSelection(Long selectedRowId, DatasetRow selectedRow, ColumnMetadata columnMetadata) {
        this.selectedRowId = selectedRowId;
        this.selectedRow = selectedRow;
        this.columnMetadata = columnMetadata;
    }

    public Long getSelectedRowId() {
        return selectedRowId;
    }

    public DatasetRow getSelectedRow() {
        return selectedRow;
    }

    public ColumnMetadata getColumnMetadata() {
        return columnMetadata;
    }
}
