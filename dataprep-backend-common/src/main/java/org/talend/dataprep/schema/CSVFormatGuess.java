package org.talend.dataprep.schema;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.talend.dataprep.schema.io.CSVSchemaParser;
import org.talend.dataprep.schema.io.CSVSerializer;
import org.talend.dataprep.schema.io.NoOpDraftValidator;

@Service(CSVFormatGuess.BEAN_ID)
public class CSVFormatGuess implements FormatGuess {

    public static final String SEPARATOR_PARAMETER = "SEPARATOR"; //$NON-NLS-1$

    protected static final String BEAN_ID = "formatGuess#csv";

    @Autowired
    private CSVSchemaParser schemaParser;

    @Autowired
    private CSVSerializer serializer;

    @Autowired
    private NoOpDraftValidator noDraftValidator;

    public CSVFormatGuess() {
        //
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
    public SchemaParser getSchemaParser() {
        return this.schemaParser;
    }

    @Override
    public Serializer getSerializer() {
        return this.serializer;
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
