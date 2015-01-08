package org.talend.dataprep.dataset.service.analysis.schema;

/**
 * Represents a "guess" for a data set content format (e.g. CSV, Excel...).
 * 
 * @see org.talend.dataprep.dataset.service.analysis.SchemaAnalysis
 */
public interface FormatGuess {

    /**
     * @return The MIME type of the format guess.
     */
    String getMediaType();

    /**
     * @return A float between 0 and 1. 1 indicates guess is 100% sure, 0 indicates a certain 0%.
     */
    float getConfidence();

    /**
     * @return A {@link org.talend.dataprep.dataset.service.analysis.schema.SchemaParser} that allowed data prep to read
     * {@link org.talend.dataprep.dataset.objects.ColumnMetadata column metadata} information from the data set.
     */
    SchemaParser getSchemaParser();

    /**
     * @return A {@link org.talend.dataprep.dataset.service.analysis.schema.Serializer serializer} able to transform the
     * underlying data set content into JSON stream.
     * @see org.talend.dataprep.dataset.store.DataSetContentStore#get(org.talend.dataprep.dataset.objects.DataSetMetadata)
     */
    Serializer getSerializer();
}
