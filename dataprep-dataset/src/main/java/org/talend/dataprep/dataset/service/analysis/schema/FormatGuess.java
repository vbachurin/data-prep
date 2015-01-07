package org.talend.dataprep.dataset.service.analysis.schema;

public interface FormatGuess {
    /**
     * @return
     */
    String getMediaType();

    /**
     * @return
     */
    float getConfidence();

    /**
     * @return
     */
    SchemaParser getSchemaParser();

    /**
     * @return
     */
    Serializer getSerializer();
}
