package org.talend.dataprep.transformation.api.transformer.exporter;

import org.talend.dataprep.api.type.ExportType;

import java.util.HashMap;

/**
 * Export configuration. It holds the parameters that configures the wanted export
 */
public class ExportConfiguration {

    /**
     * The export format {@link org.talend.dataprep.api.type.ExportType}
     */
    private final ExportType format;

    /**
     * The actions in JSON string format
     */
    private final String actions;

    /**
     * The arguments in HashMap
     */
    private final HashMap<String,Object> arguments ;



    /**
     * The constructor
     * 
     * @param format The export type.
     * @param actions The actions in JSON string format.
     * @param arguments Arguments for the Exporter
     */
    protected ExportConfiguration(final ExportType format, final String actions, final HashMap<String, Object> arguments) {
        this.format = format;
        this.actions = actions;
        this.arguments = arguments;
    }

    /**
     * Create an ExportConfiguration builder
     * 
     * @return The builder
     */
    public static Builder builder() {
        return new Builder();
    }

    public ExportType getFormat() {
        return format;
    }

    public String getActions() { return actions; }

    public HashMap<String,Object> getArguments() { return arguments; }

    /**
     * Export configuration builder
     */
    public static class Builder {

        /**
         * The export format {@link org.talend.dataprep.api.type.ExportType}
         */
        protected ExportType format;

        /**
         * The actions in JSON string format
         */
        protected String actions;

        /**
         * The actions in HashMap
         */
        protected HashMap<String,Object> arguments;

        /**
         * Builder DSL for format setter
         * 
         * @param format The export type.
         * @return The builder
         */
        public Builder format(final ExportType format) {
            this.format = format;
            return this;
        }

        /**
         * Builder DSL for actions setter
         * 
         * @param actions The actions in JSON string format.
         * @return The builder
         */
        public Builder actions(final String actions) {
            this.actions = actions;
            return this;
        }

        /**
         * Builder DSL for arguments setter
         * @param arguments The arguments in HashMap
         * @return The builder
         */
        public Builder args(final HashMap<String, Object> arguments) {
            this.arguments = arguments;
            return this;
        }

        /**
         * Create an Export Configuration
         * 
         * @return The configuration
         */
        public ExportConfiguration build() {
            return new ExportConfiguration(format, actions, arguments);
        }


    }
}
