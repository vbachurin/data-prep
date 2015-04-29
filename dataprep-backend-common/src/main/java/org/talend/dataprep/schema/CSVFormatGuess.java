package org.talend.dataprep.schema;

import java.beans.Transient;
import java.util.Collections;
import java.util.Map;

public class CSVFormatGuess implements FormatGuess {

    public static final String SEPARATOR_PARAMETER = "SEPARATOR"; //$NON-NLS-1$

    private final Separator sep;

    public CSVFormatGuess(Separator sep) {
        this.sep = sep;
    }

    @Override
    public String getMediaType() {
        return "text/csv"; //$NON-NLS-1$
    }

    @Override
    public float getConfidence() {
        return 1;
    }

    @Override
    public Map<String, String> getParameters() {
        return Collections.singletonMap(SEPARATOR_PARAMETER, String.valueOf(sep.separator));
    }

    @Override
    @Transient
    public String getParserService() {
        return "parser#csv"; //$NON-NLS-1$
    }

    @Override
    @Transient
    public String getSerializerService() {
        return "serializer#csv"; //$NON-NLS-1$
    }

}
