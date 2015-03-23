package org.talend.dataprep.api;

import java.io.InputStream;
import java.io.Writer;
import java.util.*;

import org.apache.commons.io.IOUtils;
import org.springframework.data.annotation.Id;
import org.talend.dataprep.api.json.DataSetMetadataModule;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Represents all information needed to look for a data set ({@link #getId()} as well as information inferred from data
 * set content:
 * <ul>
 * <li>Metadata information: see {@link #getRow()}</li>
 * <li>Current progress on content processing:: see {@link #getLifecycle()}</li>
 * </ul>
 * 
 * @see DataSetMetadata.Builder
 */
public class DataSetMetadata {

    @Id
    private final String id;

    private final RowMetadata rowMetadata;

    private final DataSetLifecycle lifecycle = new DataSetLifecycle();

    private final DataSetContent content = new DataSetContent();

    private final String name;

    private final String author;

    private final long creationDate;

    public DataSetMetadata(String id, String name, String author, long creationDate, RowMetadata rowMetadata) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.creationDate = creationDate;
        this.rowMetadata = rowMetadata;
    }

    /**
     * @param json A valid JSON stream, may be <code>null</code>.
     * @return The {@link DataSetMetadata} instance parsed from stream or <code>null</code> if parameter is null. If
     * stream is empty, also returns <code>null</code>.
     */
    public static DataSetMetadata from(InputStream json) {
        if (json == null) {
            return null;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(DataSetMetadataModule.DEFAULT);
            String jsonString = IOUtils.toString(json).trim();
            if (jsonString.isEmpty()) {
                return null; // Empty stream
            }
            return mapper.reader(DataSetMetadata.class).readValue(jsonString);
        } catch (Exception e) {
            throw new RuntimeException("Unable to parse '" + json + "'.", e);
        }
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
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")); //$NON-NLS-1$
        calendar.setTimeInMillis(creationDate);
        return calendar.getTime();
    }

    /**
     * Writes the current {@link DataSetMetadata} to <code>writer</code> as JSON format.
     *
     * @param writer A non-null writer.
     */
    public void to(Writer writer) {
        if (writer == null) {
            throw new IllegalArgumentException("Writer cannot be null.");
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(DataSetMetadataModule.DEFAULT);
            mapper.writer().writeValue(writer, this);
            writer.flush();
        } catch (Exception e) {
            throw new RuntimeException("Unable to serialize object to JSON.", e);
        }
    }

    public static class Builder {

        private String id;

        private ColumnMetadata.Builder[] columnBuilders;

        private String author = "anonymous";

        private String name = "";

        private long createdDate = System.currentTimeMillis();

        private int size;

        private int headerSize;

        private int footerSize;

        private boolean contentAnalyzed;

        private boolean schemaAnalyzed;

        private boolean qualityAnalyzed;

        public static DataSetMetadata.Builder metadata() {
            return new Builder();
        }

        public DataSetMetadata.Builder id(String id) {
            this.id = id;
            return this;
        }

        public DataSetMetadata.Builder author(String author) {
            this.author = author;
            return this;
        }

        public DataSetMetadata.Builder name(String name) {
            this.name = name;
            return this;
        }

        public DataSetMetadata.Builder created(long createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        public DataSetMetadata.Builder row(ColumnMetadata.Builder... columns) {
            columnBuilders = columns;
            return this;
        }

        public DataSetMetadata.Builder size(int size) {
            this.size = size;
            return this;
        }

        public DataSetMetadata.Builder headerSize(int headerSize) {
            this.headerSize = headerSize;
            return this;
        }

        public DataSetMetadata.Builder footerSize(int footerSize) {
            this.footerSize = footerSize;
            return this;
        }

        public Builder contentAnalyzed(boolean contentAnalyzed) {
            this.contentAnalyzed = contentAnalyzed;
            return this;
        }

        public Builder schemaAnalyzed(boolean schemaAnalyzed) {
            this.schemaAnalyzed = schemaAnalyzed;
            return this;
        }

        public Builder qualityAnalyzed(boolean qualityAnalyzed) {
            this.qualityAnalyzed = qualityAnalyzed;
            return this;
        }

        public DataSetMetadata build() {
            if (id == null) {
                throw new IllegalStateException("No id set for dataset.");
            }
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
            DataSetMetadata metadata = new DataSetMetadata(id, name, author, createdDate, row);
            // Content information
            DataSetContent content = metadata.getContent();
            content.setNbRecords(size);
            content.setNbLinesInHeader(headerSize);
            content.setNbLinesInFooter(footerSize);
            // Lifecycle information
            DataSetLifecycle lifecycle = metadata.getLifecycle();
            lifecycle.contentIndexed(contentAnalyzed);
            lifecycle.schemaAnalyzed(schemaAnalyzed);
            lifecycle.qualityAnalyzed(qualityAnalyzed);
            return metadata;
        }
    }

}
