package org.talend.dataprep.transformation.api.transformer.configuration;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.talend.dataprep.api.dataset.DataSet;
import org.talend.dataprep.api.type.ExportType;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;
import org.talend.dataprep.transformation.api.transformer.TransformerWriter;

/**
 * Full configuration for a transformation.
 */
public class Configuration {

    /** The export format {@link org.talend.dataprep.api.type.ExportType} */
    private final ExportType format;

    /** The actions in JSON string format */
    private final String actions;

    /** The arguments for export */
    private final Map<String, Object> arguments;

    /** The dataset input to transform. */
    private final DataSet input;

    /** Where to write the transformed content. */
    private final TransformerWriter output;

    /** List of transformation context, one per action. */
    private TransformationContext transformationContext;

    /**
     * Constructor for the transformer configuration.
     * 
     * @param input the json parser.
     * @param output the writer plugged to the output stream to write into.
     */
    protected Configuration(final DataSet input, //
                            final TransformerWriter output, //
                            final ExportType format, //
                            final String actions, //
                            final Map<String, Object> arguments) {
        this.input = input;
        this.output = output;
        this.format = format;
        this.actions = actions;
        this.arguments = arguments;
        this.transformationContext = new TransformationContext();
    }

    /**
     * @return a TransformerConfiguration builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * @return The expected output {@link ExportType format} of the transformation.
     */
    public ExportType format() {
        return format;
    }

    /**
     * @return The actions (as JSON string) to apply in transformation.
     */
    public String getActions() {
        return actions;
    }

    /**
     * @return Arguments for the {@link ExportType export type} (parameters depend on the export type).
     */
    public Map<String, Object> getArguments() {
        return arguments;
    }

    /**
     * @return the dataset to transform as json parser.
     */
    public DataSet input() {
        return input;
    }

    /**
     * @return the writer where to write the transform dataset.
     */
    public TransformerWriter output() {
        return output;
    }

    /**
     * @return the transformation context that match the given index.
     */
    public TransformationContext getTransformationContext() {
        return transformationContext;
    }

    /**
     * Builder pattern used to simplify code writing.
     */
    public static class Builder {

        /**
         * The export format {@link org.talend.dataprep.api.type.ExportType}
         */
        private ExportType format = ExportType.JSON;

        /**
         * The actions in JSON string format
         */
        private String actions = StringUtils.EMPTY;

        /**
         * The actions in Map
         */
        private Map<String, Object> arguments = Collections.emptyMap();

        /** The dataset input to transform. */
        private DataSet input = DataSet.empty();

        /** Where to write the transformed content. */
        private TransformerWriter output;

        /**
         * @param input the dataset input to set.
         * @return the builder to chain calls.
         */
        public Builder input(final DataSet input) {
            this.input = input;
            return this;
        }

        /**
         * @param output where to write the transformed dataset.
         * @return the builder to chain calls.
         */
        public Builder output(final TransformerWriter output) {
            this.output = output;
            return this;
        }

        /**
         * @return a new {@link Configuration} from the builder setup.
         */
        public Configuration build() {
            return new Configuration(input, output, format, actions, arguments);
        }

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
        public Builder withActions(final String actions) {
            this.actions = actions;
            return this;
        }

        /**
         * Builder DSL for arguments setter
         *
         * @param arguments The arguments in Map
         * @return The builder
         */
        public Builder args(final Map<String, Object> arguments) {
            this.arguments = arguments;
            return this;
        }

    }
}
