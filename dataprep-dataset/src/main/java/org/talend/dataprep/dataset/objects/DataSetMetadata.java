package org.talend.dataprep.dataset.objects;

import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataSetMetadata {

    @Id
    private final String id;

    private final RowMetadata rowMetadata;

    private DataSetMetadata(String id, RowMetadata rowMetadata) {
        this.id = id;
        this.rowMetadata = rowMetadata;
    }

    public String getId() {
        return id;
    }

    public RowMetadata getRow() {
        return rowMetadata;
    }

    public static class Builder {

        private final String id;

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
