package org.talend.dataprep.transformation.api.transformer.exporter.csv;

import static au.com.bytecode.opencsv.CSVWriter.DEFAULT_SEPARATOR;

import org.talend.dataprep.api.type.ExportType;
import org.talend.dataprep.transformation.api.transformer.exporter.ExportConfiguration;

/**
 * CSV specific export parameters
 */
public class CsvExportConfiguration extends ExportConfiguration {
    /**
     * The CSV separator
     */
    private final Character csvSeparator;

    /**
     * The constructor
     * @param format The export type.
     * @param actions The actions in JSON string format.
     * @param csvSeparator The csv separator
     */
    private CsvExportConfiguration(final ExportType format, final String actions, final Character csvSeparator) {
        super(format, actions);
        this.csvSeparator = csvSeparator == null ? DEFAULT_SEPARATOR : csvSeparator;
    }

    /**
     * Create a CSV export configuration builder
     * @return The builder
     */
    public static Builder builder() {
        return new Builder();
    }

    public Character getCsvSeparator() {
        return csvSeparator;
    }

    /**
     * CSV Export configuration builder
     */
    public static class Builder extends ExportConfiguration.Builder {
        /**
         * The CSV separator
         */
        private Character csvSeparator;

        /**
         * Builder DSL for csvSeparator setter
         * @param csvSeparator The CSV separator to use.
         * @return The builder
         */
        public Builder csvSeparator(final Character csvSeparator) {
            this.csvSeparator = csvSeparator;
            return this;
        }

        /**
         * Create a CSV Export Configuration
         * @return The configuration
         */
        public CsvExportConfiguration build() {
            return new CsvExportConfiguration(format, actions, csvSeparator);
        }
    }
}
