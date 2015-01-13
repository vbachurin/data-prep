package org.talend.dataprep.dataset.objects;

import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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

    private String                 name;

    private String                 author;

    private Date                   creationDate;

    public DataSetMetadata(String id, String name, String author, Date creationDate, RowMetadata rowMetadata) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.creationDate = creationDate;
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

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public static class Builder {

        private final String             id;

        private ColumnMetadata.Builder[] columnBuilders;

        private String                   author = "anonymous";

        private String                   name = "";

        private Date                     createdDate = new Date(System.currentTimeMillis());

        public Builder(String id) {
            this.id = id;
        }

        public static DataSetMetadata.Builder id(String id) {
            return new Builder(id);
        }

        public DataSetMetadata.Builder author(String author) {
            this.author = author;
            return this;
        }

        public DataSetMetadata.Builder name(String name) {
            this.name = name;
            return this;
        }

        public DataSetMetadata.Builder created(Date createdDate) {
            this.createdDate = createdDate;
            return this;
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
            return new DataSetMetadata(id, name, author, createdDate, row);
        }

    }
}
