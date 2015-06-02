package org.talend.dataprep.api.type;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@JsonSerialize(using = ExportType.ExportTypeSerializer.class)
public enum ExportType {
    CSV("text/csv", ".csv"),
    XLS("application/vnd.ms-excel", ".xls"),
    TABLEAU("", "");

    private final String mimeType;

    private final String extension;

    ExportType(final String mimeType, final String extension) {
        this.mimeType = mimeType;
        this.extension = extension;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getExtension() {
        return extension;
    }


    public static class ExportTypeSerializer extends JsonSerializer<ExportType>
    {
        @Override
        public void serialize(ExportType value, JsonGenerator jgen, SerializerProvider provider) throws IOException,
            JsonProcessingException
        {
            Map<String, String> map = new HashMap<>();
            map.put("mimeType", value.getMimeType());
            map.put("extension", value.getExtension());
            map.put( "id", value.name() );
            jgen.writeObject(map);
        }
    }
}
