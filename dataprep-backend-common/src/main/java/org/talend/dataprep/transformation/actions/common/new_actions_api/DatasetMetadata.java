package org.talend.dataprep.transformation.actions.common.new_actions_api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
