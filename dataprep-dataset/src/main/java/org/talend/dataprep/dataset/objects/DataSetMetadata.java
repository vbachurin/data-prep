package org.talend.dataprep.dataset.objects;

import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents all information needed to look for a data set ({@link #getId()} as well as information inferred from data
 * set content:
 * <ul>
 * <li>Metadata information: see {@link #getRow()}</li>
 * <li>Current progress on content processing:: see {@link #getLifecycle()}</li>
 * </ul>
 * 
 * @see org.talend.dataprep.dataset.objects.DataSetMetadata.Builder
 */
public class DataSetMetadata {

    @Id
    private final String           id;

    private final RowMetadata      rowMetadata;

    private final DataSetLifecycle lifecycle = new DataSetLifecycle();

    private final DataSetContent   content   = new DataSetContent();

    public DataSetMetadata(String id, RowMetadata rowMetadata) {
        this.id = id;
        this.rowMetadata = rowMetadata;
    }

    public String getId() {
        return id;
    }

    public RowMetadata getRow() {
        return rowMetadata;
    }

    public DataSetLifecycle getLifecycle() {
        return lifecycle;
    }

    public DataSetContent getContent() {
        return content;
    }

    public static class Builder {

        private final String             id;

        private ColumnMetadata.Builder[] columnBuilders;

        public Builder(String id) {
            this.id = id;
        }

        public static DataSetMetadata.Builder id(String id) {
            return new Builder(id);
        }

        public DataSetMetadata.Builder row(ColumnMetadata.Builder... columns) {
            columnBuilders = columns;
            return this;
        }

        public DataSetMetadata build() {
            List<ColumnMetadata> columns;
            if (columnBuilders != null) {
                columns = new ArrayList<>();
                for (ColumnMetadata.Builder columnBuilder : columnBuilders) {
                    columns.add(columnBuilder.build());
                }
            } else {
                columns = Collections.emptyList();
            }
            RowMetadata row = new RowMetadata(columns);
            return new DataSetMetadata(id, row);
        }

    }
}
