package org.talend.dataprep.api.type;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = ExportType.ExportTypeSerializer.class)
public enum ExportType {
    // take care when declaring new export type as only one can be default :-)
    CSV("text/csv", ".csv", true, false, Arrays.asList(new Parameter("csvSeparator", "CHOOSE_SEPARATOR", "radio",
            new ParameterValue(";", "SEPARATOR_SEMI_COLON"), //
            Arrays.asList(new ParameterValue("\u0009", "SEPARATOR_TAB"), // &#09;
                    new ParameterValue(" ", "SEPARATOR_SPACE"), //
                    new ParameterValue(",", "SEPARATOR_COMMA"))))),
    XLS("application/vnd.ms-excel", ".xls", false, true, Collections.<Parameter> emptyList()),
    TABLEAU("application/tde", ".tde", false, false, Collections.<Parameter> emptyList()),
    JSON("application.json", ".json", false, false, Collections.<Parameter> emptyList());


    private final String mimeType;

    private final String extension;

    /**
     * does this export type need more parameters? (ui will open a new form in this case)
     */
    private final boolean needParameters;

    /**
     * is it the default export
     */
    private final boolean defaultExport;

    /**
     * list of extra parameters needed for this export (i.e separator for csv files etc...)
     */
    private final List<Parameter> parameters;

    ExportType(final String mimeType, final String extension, final boolean needParameters, final boolean defaultExport,
            final List<Parameter> parameters) {
        this.mimeType = mimeType;
        this.extension = extension;
        this.needParameters = needParameters;
        this.defaultExport = defaultExport;
        this.parameters = parameters;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getExtension() {
        return extension;
    }

    public boolean isNeedParameters() {
        return needParameters;
    }

    public boolean isDefaultExport() {
        return defaultExport;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public static class Parameter {

        /**
         * parameter name
         */
        private final String name;

        /**
         * can be used as a label key in the ui
         */
        private final String labelKey;

        /**
         * the default value for the parameter must not be in values
         */
        private final ParameterValue defaultValue;

        /**
         * all possible values for the parameter
         */
        private final List<ParameterValue> values;

        /**
         * html type (input type: radio, text)
         */
        private final String type;

        public Parameter(String name, String labelKey, String type, ParameterValue defaultValue, List<ParameterValue> values) {
            this.name = name;
            this.labelKey = labelKey;
            this.defaultValue = defaultValue;
            this.values = values;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public String getLabelKey() {
            return labelKey;
        }

        public ParameterValue getDefaultValue() {
            return defaultValue;
        }

        public List<ParameterValue> getValues() {
            return values;
        }

        public String getType() {
            return type;
        }
    }

    private static class ParameterValue {

        private final String value;

        private final String labelKey;

        public ParameterValue(String value, String labelKey) {
            this.value = value;
            this.labelKey = labelKey;
        }

        public String getValue() {
            return value;
        }

        public String getLabelKey() {
            return labelKey;
        }
    }

    public static class ExportTypeSerializer extends JsonSerializer<ExportType> {

        @Override
        public void serialize(ExportType value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeStartObject();

            jgen.writeStringField("mimeType", value.getMimeType());
            jgen.writeStringField("extension", value.getExtension());
            jgen.writeStringField("id", value.name());
            jgen.writeStringField("needParameters", Boolean.toString(value.isNeedParameters()));
            jgen.writeStringField("defaultExport", Boolean.toString(value.isDefaultExport()));

            if (!value.getParameters().isEmpty()) {
                jgen.writeFieldName("parameters");
                jgen.writeStartArray(value.getParameters().size());
                for (Parameter parameter : value.getParameters()) {
                    jgen.writeStartObject();
                    jgen.writeStringField("name", parameter.getName());
                    jgen.writeStringField("labelKey", parameter.getLabelKey());
                    jgen.writeStringField( "type", parameter.getType() );
                    jgen.writeFieldName("defaultValue");
                    jgen.writeStartObject();
                    jgen.writeStringField("value", parameter.getDefaultValue().getValue());
                    jgen.writeStringField("labelKey", parameter.getDefaultValue().getLabelKey());

                    jgen.writeEndObject();

                    if (!parameter.getValues().isEmpty()) {
                        jgen.writeFieldName("values");
                        jgen.writeStartArray(parameter.getValues().size());
                        for (ParameterValue parameterValue : parameter.getValues()) {
                            jgen.writeStartObject();
                            jgen.writeStringField("value", parameterValue.getValue());
                            jgen.writeStringField("labelKey", parameterValue.getLabelKey());

                            jgen.writeEndObject();
                        }

                        jgen.writeEndArray();

                    }

                    jgen.writeEndObject();
                }
                jgen.writeEndArray();
            }

            jgen.writeEndObject();
        }
    }
}
