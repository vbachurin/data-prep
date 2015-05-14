package org.talend.dataprep.schema;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.talend.dataprep.schema.io.NoOpDraftValidator;
import org.talend.dataprep.schema.io.NoOpParser;
import org.talend.dataprep.schema.io.NoOpSerializer;

@Component(NoOpFormatGuess.BEAN_ID)
public class NoOpFormatGuess implements FormatGuess {

    protected static final String BEAN_ID = "formatGuess#any";

    @Autowired
    private NoOpSerializer serializer;

    @Autowired
    private NoOpParser parser;

    @Autowired
    private NoOpDraftValidator noDraftValidator;

    @Override
    public String getMediaType() {
        return "*/*"; //$NON-NLS-1$
    }

    @Override
    public float getConfidence() {
        return 0;
    }

    @Override
    public Serializer getSerializer() {
        return serializer;
    }

    @Override
    public SchemaParser getSchemaParser() {
        return parser;
    }

    @Override
    public DraftValidator getDraftValidator() {
        return noDraftValidator;
    }

    @Override
    public String getBeanId() {
        return BEAN_ID;
    }
}
