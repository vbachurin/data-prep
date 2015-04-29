package org.talend.dataprep.schema;

import java.beans.Transient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.talend.dataprep.schema.io.CSVSchemaParser;
import org.talend.dataprep.schema.io.CSVSerializer;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.exception.CommonErrorCodes;
import org.talend.dataprep.exception.TDPException;

    public static final String SEPARATOR_PARAMETER = "SEPARATOR"; //$NON-NLS-1$

    protected static final String BEAN_ID = "formatGuess#csv";

    @Autowired
    private CSVSchemaParser schemaParser;

    @Autowired
    private CSVSerializer serializer;

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
    public SchemaParser getSchemaParser()
    {
        return this.schemaParser;
    }

    @Override
    public Serializer getSerializer()
    {
        return this.serializer;
    }

    @Override
    public String getBeanId() {
        return BEAN_ID;
    }
}
