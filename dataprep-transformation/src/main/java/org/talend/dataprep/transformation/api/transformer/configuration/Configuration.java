package org.talend.dataprep.transformation.api.transformer.configuration;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.talend.dataprep.api.type.ExportType;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.TransformationErrorCodes;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;
import org.talend.dataprep.transformation.api.transformer.TransformerWriter;
import org.talend.dataprep.transformation.api.transformer.writer.CsvWriter;
import org.talend.dataprep.transformation.api.transformer.writer.JsonWriter;
import org.talend.dataprep.transformation.api.transformer.writer.TableauWriter;
import org.talend.dataprep.transformation.api.transformer.writer.XlsWriter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Full configuration for a transformation.
 */
public class Configuration {

    public enum Volume {
        LARGE,
        SMALL
    }
    
    /** The export format {@link org.talend.dataprep.api.type.ExportType} */
    private final ExportType format;

    /** The actions in JSON string format */
    private final String actions;

    /** The arguments for export */
    private final Map<String, Object> arguments;

    /** Where to write the transformed content. */
    private final OutputStream output;

    private final Volume dataVolume;

    /** List of transformation context, one per action. */
    private TransformationContext transformationContext;

    /**
     * Constructor for the transformer configuration.
     */
    protected Configuration(final OutputStream output, //
            final ExportType format, //
            final String actions, //
            final Map<String, Object> arguments, //
            final Volume dataVolume) {
        this.output = output;
        this.format = format;
        this.actions = actions;
        this.arguments = arguments;
        this.dataVolume = dataVolume;
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
     * @return the writer where to write the transform dataset.
     */
    public OutputStream output() {
        return output;
    }

    /**
     * @return the transformation context that match the given index.
     */
    public TransformationContext getTransformationContext() {
        return transformationContext;
    }

    /**
     * @return The {@link TransformerWriter writer} to be used to send result back to client.
     */
    public TransformerWriter writer() {
        switch (format) {
        case CSV:
            String separator = (String) arguments.get("exportParameters.csvSeparator");
            if (separator == null) {
                separator = ",";
            }
            return new CsvWriter(output, separator.charAt(0));
        case XLS:
            return new XlsWriter(output);
        case TABLEAU:
            return new TableauWriter(output);
        case JSON:
            try {
                final ObjectMapper mapper = new ObjectMapper();
                final JsonGenerator generator = mapper.getFactory().createGenerator(output);
                return new JsonWriter(generator);
            } catch (IOException e) {
                throw new TDPException(TransformationErrorCodes.UNEXPECTED_EXCEPTION, e);
            }
        default:
            throw new TDPException(TransformationErrorCodes.OUTPUT_TYPE_NOT_SUPPORTED);
        }
    }

    public Volume volume() {
        return dataVolume;
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

        /** Where to write the transformed content. */
        private OutputStream output;

        /** Gives hint on the amount of data the transformer may expect */
        private Volume dataVolume = Volume.SMALL;

        /**
         * @param output where to write the transformed dataset.
         * @return the builder to chain calls.
         */
        public Builder output(final OutputStream output) {
            this.output = output;
            return this;
        }

        /**
         * @return a new {@link Configuration} from the builder setup.
         */
        public Configuration build() {
            return new Configuration(output, format, actions, arguments, dataVolume);
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
        public Builder actions(final String actions) {
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

        public Builder volume(Volume dataVolume) {
            this.dataVolume = dataVolume;
            return this;
        }

    }
}
