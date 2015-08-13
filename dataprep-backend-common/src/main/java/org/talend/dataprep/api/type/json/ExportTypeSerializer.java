package org.talend.dataprep.api.type.json;

import java.io.IOException;

import org.talend.dataprep.api.type.ExportType;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Export type JSON parameter.
 */
public class ExportTypeSerializer extends JsonSerializer<ExportType> {

    /** Constant used to the 'labelKey' */
    private static final String LABEL = "labelKey";

    /**
     * @see ExportTypeSerializer#serialize(ExportType, JsonGenerator, SerializerProvider)
     */
    @Override
    public void serialize(ExportType value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();

        gen.writeStringField("mimeType", value.getMimeType());
        gen.writeStringField("extension", value.getExtension());
        gen.writeStringField("id", value.name());
        gen.writeStringField("needParameters", Boolean.toString(value.isNeedParameters()));
        gen.writeStringField("defaultExport", Boolean.toString(value.isDefaultExport()));

        if (!value.getParameters().isEmpty()) {
            gen.writeFieldName("parameters");
            gen.writeStartArray(value.getParameters().size());
            for (ExportType.Parameter parameter : value.getParameters()) {
                gen.writeStartObject();
                gen.writeStringField("name", parameter.getName());
                gen.writeStringField(LABEL, parameter.getLabelKey());
                gen.writeStringField("type", parameter.getType());
                gen.writeFieldName("defaultValue");
                gen.writeStartObject();
                gen.writeStringField("value", parameter.getDefaultValue().getValue());
                gen.writeStringField(LABEL, parameter.getDefaultValue().getLabelKey());
                gen.writeEndObject();

                writeParameterValues(gen, parameter);

                gen.writeEndObject();
            }
            gen.writeEndArray();
        }

        gen.writeEndObject();
    }

    /**
     * Write parameters values.
     * 
     * @param gen the json generator to use.
     * @param parameter the parameter to serialize.
     * @throws IOException if an error occurs.
     */
    private void writeParameterValues(JsonGenerator gen, ExportType.Parameter parameter) throws IOException {
        if (parameter.getValues().isEmpty()) {
            return;
        }

        gen.writeFieldName("values");
        gen.writeStartArray(parameter.getValues().size());
        for (ExportType.ParameterValue parameterValue : parameter.getValues()) {
            gen.writeStartObject();
            gen.writeStringField("value", parameterValue.getValue());
            gen.writeStringField(LABEL, parameterValue.getLabelKey());

            gen.writeEndObject();
        }
        gen.writeEndArray();
    }

}
