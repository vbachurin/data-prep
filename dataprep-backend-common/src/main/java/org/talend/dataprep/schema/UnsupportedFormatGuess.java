package org.talend.dataprep.schema;

import org.springframework.stereotype.Component;

/**
 * A special implementation of {@link FormatGuess} to serve as fallback and indicates the provided content is not
 * supported in data prep.
 */
@Component(UnsupportedFormatGuess.BEAN_ID)
public class UnsupportedFormatGuess implements FormatGuess {

    protected static final String BEAN_ID = "formatGuess#any";

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
        throw new UnsupportedOperationException();
    }

    @Override
    public SchemaParser getSchemaParser() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DraftValidator getDraftValidator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getBeanId() {
        return BEAN_ID;
    }
}
