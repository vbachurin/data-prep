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
    CSV("text/csv", ".csv", true, false, Arrays.asList("csvSeparator")),
    XLS("application/vnd.ms-excel", ".xls", false, true, Collections.<String> emptyList()),
    TABLEAU("application/tde", ".tde", false, false, Collections.<String> emptyList());

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
    private final List<String> parametersNames;

    ExportType(final String mimeType, final String extension, final boolean needParameters, final boolean defaultExport,
            final List<String> parametersNames) {
        this.mimeType = mimeType;
        this.extension = extension;
        this.needParameters = needParameters;
        this.defaultExport = defaultExport;
        this.parametersNames = parametersNames;
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

    public List<String> getParametersNames() {
        return parametersNames;
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

            if (!value.getParametersNames().isEmpty()) {
                jgen.writeFieldName( "parametersNames" );
                jgen.writeStartArray(value.getParametersNames().size());
                for (String s : value.getParametersNames()) {
                    jgen.writeString(s);
                }
                jgen.writeEndArray();
            }

            jgen.writeEndObject();
        }
    }
}
