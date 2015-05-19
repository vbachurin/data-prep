package org.talend.dataprep.api.dataset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

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

    /** The dataset id. */
    @Id
    private String id;

    /** Row description. */
    @JsonIgnore(true)
    private RowMetadata rowMetadata;

    /** Dataset life cycle status. */
    @JsonProperty("lifecycle")
    private final DataSetLifecycle lifecycle = new DataSetLifecycle();

    /** Dataset content summary. */
    @JsonProperty("content")
    @JsonUnwrapped
    private final DataSetContent content = new DataSetContent();

    /** Dataset governance. */
    @JsonProperty("governance")
    @JsonUnwrapped
    private final DataSetGovernance governance = new DataSetGovernance();

    /** Dataset name. */
    @JsonProperty("name")
    private String name;

    /** Dataset author. */
    @JsonProperty("author")
    private String author;

    @JsonProperty("created")
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM-dd-YYYY HH:mm", timezone="UTC")
    private Date creationDate;

    /** Sheet number in case of excel source. */
    @JsonProperty("sheetNumber")
    private int sheetNumber;

    public DataSetMetadata() {
    }

    /**
     * Constructor.
     * 
     * @param id dataset id.
     * @param name dataset name.
     * @param author dataset author.
     * @param creationDate dataset creation date.
     * @param rowMetadata row metadata.
     */
    public DataSetMetadata(String id, String name, String author, long creationDate, RowMetadata rowMetadata) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.creationDate = new Date(creationDate);
        this.rowMetadata = rowMetadata;
    }

    /**
     * @return the dataset id.
     */
    public String getId() {
        return id;
    }
    
    /**
     * @return the dataset row description.
     */
    @JsonIgnore(true)
    public RowMetadata getRow() {
        return rowMetadata;
    }

    /**
     * @return the dataset lifecycle.
     */
    public DataSetLifecycle getLifecycle() {
        return lifecycle;
    }

    /**
     * @return the dataset content summary.
     */
    public DataSetContent getContent() {
        return content;
    }

    /**
     * @return the dataset governance.
     */
    public DataSetGovernance getGovernance() {
        return this.governance;
    }

    /**
     * @return the dataset name.
     */
    public String getName() {
        return name;
    }

    /**
     * @return the dataset author.
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @return the dataset sheet number in case of excel source.
     */
    public int getSheetNumber() {
        return sheetNumber;
    }

    /**
     * @return the dataset creation date.
     */
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * Dataset builder.
     */
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

        private int sheetNumber;

        private String formatGuessId;

        private String mediaType;

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

        public Builder sheetNumber(int sheetNumber) {
            this.sheetNumber = sheetNumber;
            return this;
        }

        public Builder formatGuessId(String formatGuessId) {
            this.formatGuessId = formatGuessId;
            return this;
        }

        public Builder mediaType(String mediaType) {
            this.mediaType = mediaType;
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
            metadata.sheetNumber = this.sheetNumber;
            // Content information
            DataSetContent content = metadata.getContent();
            content.setNbRecords(size);
            content.setNbLinesInHeader(headerSize);
            content.setNbLinesInFooter(footerSize);
            if (formatGuessId != null) {
                content.setFormatGuessId(formatGuessId);
            }
            content.setMediaType(mediaType);
            // Lifecycle information
            DataSetLifecycle lifecycle = metadata.getLifecycle();
            lifecycle.contentIndexed(contentAnalyzed);
            lifecycle.schemaAnalyzed(schemaAnalyzed);
            lifecycle.qualityAnalyzed(qualityAnalyzed);
            return metadata;
        }
    }

}
