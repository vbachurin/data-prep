package org.talend.dataprep.transformation.api.transformer.exporter;

import static au.com.bytecode.opencsv.CSVWriter.DEFAULT_SEPARATOR;

import org.talend.dataprep.api.type.ExportType;

public class ExportConfiguration {
    private final ExportType format;
    private final Character csvSeparator;
    private final String actions;

    private ExportConfiguration(final ExportType format, final Character csvSeparator, String actions) {
        this.format = format;
        this.actions = actions;
        this.csvSeparator = csvSeparator == null ? DEFAULT_SEPARATOR : csvSeparator;
    }

    public static Builder builder() {
        return new Builder();
    }

    public ExportType getFormat() {
        return format;
    }

    public Character getCsvSeparator() {
        return csvSeparator;
    }

    public String getActions() {
        return actions;
    }

    public static class Builder {
        private ExportType format;
        private Character csvSeparator;
        private String actions;

        public Builder format(final ExportType format) {
            this.format = format;
            return this;
        }

        public Builder csvSeparator(final Character csvSeparator) {
            this.csvSeparator = csvSeparator;
            return this;
        }

        public Builder actions(final String actions) {
            this.actions = actions;
            return this;
        }

        public ExportConfiguration build() {
            return new ExportConfiguration(format, csvSeparator, actions);
        }
    }
}
