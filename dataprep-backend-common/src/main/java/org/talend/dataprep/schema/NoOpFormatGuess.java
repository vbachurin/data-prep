package org.talend.dataprep.schema;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.talend.dataprep.schema.io.NoOpParser;
import org.talend.dataprep.schema.io.NoOpSerializer;

@Component(NoOpFormatGuess.BEAN_ID)
public class NoOpFormatGuess implements FormatGuess {

    protected static final String BEAN_ID = "formatGuess#any";

    @Autowired
    private NoOpSerializer serializer;

    @Autowired
    private NoOpParser parser;

    @Override
    public String getMediaType() {
        return "*/*"; //$NON-NLS-1$
    }

    @Override
    public float getConfidence() {
        return 0;
    }

    @Override
    public NoOpSerializer getSerializer()
    {
        return serializer;
    }

    public NoOpParser getSchemaParser()
    {
        return parser;
    }

    @Override
    public String getBeanId() {
        return BEAN_ID;
    }
}
