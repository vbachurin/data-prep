package org.talend.dataprep.schema;

import java.util.List;

import org.talend.dataprep.api.dataset.ColumnMetadata;

// FIXME improve to store sheet name
public class SchemaParserResult {

    private boolean draft;

    private List<ColumnMetadata> columnMetadatas;

    private int sheetNumber;

    private SchemaParserResult(boolean draft, List<ColumnMetadata> columnMetadatas, int sheetNumber) {
        this.draft = draft;
        this.columnMetadatas = columnMetadatas;
        this.sheetNumber = sheetNumber;
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

    public int getSheetNumber()
    {
        return sheetNumber;
    }

    public static class Builder {

        private boolean draft;

        private List<ColumnMetadata> columnMetadatas;

        private int sheetNumber;

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

        public Builder sheetNumber(int sheetNumber) {
            this.sheetNumber = sheetNumber;
            return this;
        }

        public SchemaParserResult build() {
            return new SchemaParserResult(this.draft, this.columnMetadatas,this.sheetNumber);
        }
    }
}
