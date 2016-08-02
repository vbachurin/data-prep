package org.talend.dataprep.transformation.actions.common.new_actions_api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Action API for a dataset metadata. From here an action should be able to read any information it might need about the dataset at
 * the states it is at the step of the action in the preparation.
 * </p>
 * The action may change:
 * <ul>
 * <li>header size</li>
 * <li>footer size</li>
 * <li>add a column</li>
 * <li>remove a column</li>
 * </ul>
 * And should be able to access columns metadata to read or modify it.
 * Some properties should be immutable once rows started to pass through the action undeleted.
 */
public class DatasetMetadata {

    private long nbRecords = 0;

    private Integer headerSize;

    private Integer footerSize;

    private List<ColumnMetadata> columns = new ArrayList<>();

    /** If the dataset is too big. I do not think it is needed in actions API. */
    private Long limit;

    public List<ColumnMetadata> getColumns() {
        return Collections.unmodifiableList(columns);
    }

    public ColumnMetadata getById(String wantedId) {
        if (wantedId != null) {
            for (ColumnMetadata column : columns) {
                if (wantedId.equals(column.getId())) {
                    return column;
                }
            }
        }
        return null;
    }

}
