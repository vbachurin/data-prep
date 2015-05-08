package org.talend.dataprep.api.dataset;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.springframework.data.annotation.Id;
import org.talend.dataprep.schema.FormatGuess;
import org.talend.dataprep.schema.SchemaParserResult;

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

    private final DataSetGovernance gov = new DataSetGovernance();

    private final String name;

    private final String author;

    private final long creationDate;

    private String sheetName;

    /**
     * if <code>true</code> this dataset is still a draft as we need more informations from the user
     */
    private boolean draft = true;

    /**
     * available only when draft is <code>true</code> i.e until some informations has been confirmed by the user
     */
    private SchemaParserResult schemaParserResult;

    public DataSetMetadata(String id, String name, String author, long creationDate, RowMetadata rowMetadata) {
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

    public DataSetGovernance getGovernance() {
        return this.gov;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName( String sheetName ) {
        this.sheetName = sheetName;
    }

    public Date getCreationDate() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")); //$NON-NLS-1$
        calendar.setTimeInMillis(creationDate);
        return calendar.getTime();
    }

    public boolean isDraft() {
        return draft;
    }

    public void setDraft(boolean draft) {
        this.draft = draft;
    }

    public SchemaParserResult getSchemaParserResult() {
        return schemaParserResult;
    }

    public void setSchemaParserResult(SchemaParserResult schemaParserResult) {
        this.schemaParserResult = schemaParserResult;
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

        private String sheetName;

        private boolean draft = true;

        private FormatGuess contentType;

        private String formatGuessId;

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

        public Builder sheetName(String sheetName) {
            this.sheetName = sheetName;
            return this;
        }

        public Builder draft(boolean draft) {
            this.draft = draft;
            return this;
        }

        public Builder formatGuessId(String formatGuessId) {
            this.formatGuessId = formatGuessId;
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
            metadata.sheetName = this.sheetName;
            metadata.draft = this.draft;
            // Content information
            DataSetContent content = metadata.getContent();
            content.setNbRecords(size);
            content.setNbLinesInHeader(headerSize);
            content.setNbLinesInFooter(footerSize);

            if (formatGuessId != null) {
                content.setFormatGuessId(formatGuessId);
            }
            // Lifecycle information
            DataSetLifecycle lifecycle = metadata.getLifecycle();
            lifecycle.contentIndexed(contentAnalyzed);
            lifecycle.schemaAnalyzed(schemaAnalyzed);
            lifecycle.qualityAnalyzed(qualityAnalyzed);
            return metadata;
        }
    }

}
