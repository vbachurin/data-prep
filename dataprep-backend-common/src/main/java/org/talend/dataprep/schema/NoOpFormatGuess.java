package org.talend.dataprep.schema;

import java.util.Collections;
import java.util.Map;

class NoOpFormatGuess implements FormatGuess {

    @Override
    public String getMediaType() {
        return "*/*"; //$NON-NLS-1$
    }

    @Override
    public float getConfidence() {
        return 0;
    }

    @Override
    public Map<String, String> getParameters() {
        return Collections.emptyMap();
    }

    @Override
    public String getParserService() {
        return "parser#any"; //$NON-NLS-1$
    }

    @Override
    public String getSerializerService() {
        return "serializer#any"; //$NON-NLS-1$
    }

}
