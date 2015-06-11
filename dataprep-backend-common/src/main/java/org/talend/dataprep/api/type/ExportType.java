package org.talend.dataprep.api.type;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = ExportType.ExportTypeSerializer.class)
public enum ExportType {
    // take care when declaring new export type as only one can be default :-)
    CSV("text/csv", ".csv", true, false),
    XLS("application/vnd.ms-excel", ".xls", false, true),
    TABLEAU("application/tde", ".tde", false, false);

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

    ExportType(final String mimeType, final String extension, final boolean needParameters,final boolean defaultExport) {
        this.mimeType = mimeType;
        this.extension = extension;
        this.needParameters = needParameters;
        this.defaultExport = defaultExport;
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

    public static class ExportTypeSerializer extends JsonSerializer<ExportType> {

        @Override
        public void serialize(ExportType value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            Map<String, String> map = new HashMap<>();
            map.put("mimeType", value.getMimeType());
            map.put("extension", value.getExtension());
            map.put("id", value.name());
            map.put("needParameters", Boolean.toString(value.isNeedParameters()));
            map.put("defaultExport", Boolean.toString(value.isDefaultExport()));
            jgen.writeObject(map);
        }
    }
}
