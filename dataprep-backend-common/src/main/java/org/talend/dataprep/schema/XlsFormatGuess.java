package org.talend.dataprep.schema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("formatGuess#xls")
public class XlsFormatGuess implements FormatGuess {

    public static final String    MEDIA_TYPE      = "application/vnd.ms-excel";

    private final Logger          logger          = LoggerFactory.getLogger(getClass());

    @Autowired
    private XlsSchemaParser xlsSchemaParser;

    public XlsFormatGuess() {
        // no op
    }

    @Override
    public String getMediaType() {
        return MEDIA_TYPE;
    }

    @Override
    public float getConfidence() {
        return 1;
    }

    @Override
    public SchemaParser getSchemaParser() {
        return xlsSchemaParser;
    }

    @Override
    public Serializer getSerializer() {
        return null;
    }
}
