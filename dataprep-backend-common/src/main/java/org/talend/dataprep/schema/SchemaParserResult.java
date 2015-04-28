package org.talend.dataprep.schema;

import java.util.List;

import org.talend.dataprep.api.dataset.ColumnMetadata;

public class SchemaParserResult {

    private boolean draft;

    private List<ColumnMetadata> columnMetadatas;

    private SchemaParserResult(boolean draft, List<ColumnMetadata> columnMetadatas) {
        this.draft = draft;
        this.columnMetadatas = columnMetadatas;
    }

    private SchemaParserResult() {
        //
    }

    public boolean draft() {
        return draft;
    }

    public List<ColumnMetadata> getColumnMetadatas() {
        return columnMetadatas;
    }

    public static class Builder {

        private boolean draft;

        private List<ColumnMetadata> columnMetadatas;

        public static SchemaParserResult.Builder parserResult() {
            return new Builder();
        }

        public Builder draft(boolean draft) {
            this.draft = draft;
            return this;
        }

        public Builder columnMetadatas(List<ColumnMetadata> columnMetadatas) {
            this.columnMetadatas = columnMetadatas;
            return this;
        }

        public SchemaParserResult build() {
            return new SchemaParserResult(this.draft, this.columnMetadatas);
        }
    }
}
