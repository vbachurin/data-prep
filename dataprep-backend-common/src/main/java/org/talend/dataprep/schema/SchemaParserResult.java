package org.talend.dataprep.schema;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.talend.dataprep.api.dataset.ColumnMetadata;

// FIXME improve to store sheet name
public class SchemaParserResult {

    private boolean draft;

    private SortedMap<String, List<ColumnMetadata>> columnMetadatas;

    private String sheetName;


    private SchemaParserResult(boolean draft, SortedMap<String, List<ColumnMetadata>> columnMetadatas, String sheetName) {
        this.draft = draft;
        this.columnMetadatas = columnMetadatas;
        this.sheetName = sheetName;
    }

    private SchemaParserResult() {
        //
    }

    public boolean draft() {
        return draft;
    }

    public SortedMap<String, List<ColumnMetadata>> getColumnMetadatas() {
        return columnMetadatas;
    }

    public String getSheetName()
    {
        return sheetName;
    }

    public static class Builder {

        private boolean draft;

        private SortedMap<String, List<ColumnMetadata>> columnMetadatas;

        private String sheetName;

        public static SchemaParserResult.Builder parserResult() {
            return new Builder();
        }

        public Builder draft(boolean draft) {
            this.draft = draft;
            return this;
        }

        public Builder columnMetadatas(SortedMap<String, List<ColumnMetadata>> columnMetadatas) {
            this.columnMetadatas = columnMetadatas;
            return this;
        }

        public Builder sheetName(String sheetName) {
            this.sheetName = sheetName;
            return this;
        }

        public SchemaParserResult build() {
            return new SchemaParserResult(this.draft, this.columnMetadatas,this.sheetName);
        }
    }
}
