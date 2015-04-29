package org.talend.dataprep.schema;

import java.util.Collections;
import java.util.Map;

public class XlsFormatGuess implements FormatGuess {

    public static final String MEDIA_TYPE = "application/vnd.ms-excel";

    @Override
    public String getMediaType() {
        return MEDIA_TYPE;
    }

    @Override
    public float getConfidence() {
        return 1;
    }

    @Override
    public Map<String, String> getParameters() {
        return Collections.emptyMap();
    }

    @Override
    public String getParserService() {
        return "parser#xls"; //$NON-NLS-1$
    }

    @Override
    public String getSerializerService() {
        return "serializer#xls"; //$NON-NLS-1$
    }
}
