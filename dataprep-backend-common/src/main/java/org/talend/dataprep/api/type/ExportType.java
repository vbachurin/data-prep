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
    CSV("text/csv", ".csv", true),
    XLS("application/vnd.ms-excel", ".xls", false),
    TABLEAU("application/tde", ".tde", false);

    private final String mimeType;

    private final String extension;

    private final boolean needParameters;

    ExportType(final String mimeType, final String extension, final boolean needParameters) {
        this.mimeType = mimeType;
        this.extension = extension;
        this.needParameters = needParameters;
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

    public static class ExportTypeSerializer extends JsonSerializer<ExportType> {

        @Override
        public void serialize(ExportType value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            Map<String, String> map = new HashMap<>();
            map.put("mimeType", value.getMimeType());
            map.put("extension", value.getExtension());
            map.put("id", value.name());
            map.put("needParameters", Boolean.toString(value.isNeedParameters()));
            jgen.writeObject(map);
        }
    }
}
