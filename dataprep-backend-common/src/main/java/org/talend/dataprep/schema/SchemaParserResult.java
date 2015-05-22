package org.talend.dataprep.schema;

import java.util.List;
import java.util.SortedMap;

import org.talend.dataprep.api.dataset.ColumnMetadata;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SchemaParserResult {

    @JsonProperty("draft")
    private boolean draft;

    @JsonProperty("sheetContents")
    private List<SheetContent> sheetContents;

    @JsonProperty("sheetName")
    private String sheetName;

    private SchemaParserResult(boolean draft, List<SheetContent> sheetContents, String sheetName) {
        this.draft = draft;
        this.sheetContents = sheetContents;
        this.sheetName = sheetName;
    }

    private SchemaParserResult() {
        //
    }

    public boolean draft() {
        return draft;
    }

    public List<SheetContent> getSheetContents()
    {
        return sheetContents;
    }

    public String getSheetName() {
        return sheetName;
    }

    public static class SheetContent {

        @JsonProperty("name")
        private String name;

        @JsonProperty("columnMetadatas")
        private List<ColumnMetadata> columnMetadatas;

        public SheetContent(String name, List<ColumnMetadata> columnMetadatas) {
            this.name = name;
            this.columnMetadatas = columnMetadatas;
        }

        public String getName() {
            return name;
        }

        public List<ColumnMetadata> getColumnMetadatas() {
            return columnMetadatas;
        }
    }

    public static class Builder {

        private boolean draft;

        private List<SheetContent> sheetContents;

        private String sheetName;

        public static SchemaParserResult.Builder parserResult() {
            return new Builder();
        }

        public Builder draft(boolean draft) {
            this.draft = draft;
            return this;
        }

        public Builder sheetContents(List<SheetContent> sheetContents) {
            this.sheetContents = sheetContents;
            return this;
        }

        public Builder sheetName(String sheetName) {
            this.sheetName = sheetName;
            return this;
        }

        public SchemaParserResult build() {
            return new SchemaParserResult(this.draft, this.sheetContents, this.sheetName);
        }
    }
}
