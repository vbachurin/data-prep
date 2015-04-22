package org.talend.dataprep.schema;

import org.springframework.stereotype.Service;

@Service("formatGuess#xls")
public class XlsFormatGuess implements FormatGuess {

    public static final String MEDIA_TYPE = "application/vnd.ms-excel";

    private XlsSchemaParser xlsSchemaParser = new XlsSchemaParser();

    private XlsSerializer xlsSerializer = new XlsSerializer();

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
        return xlsSerializer;
    }
}
