package org.talend.dataprep.dataset.service.analysis.schema;

import org.talend.dataprep.dataset.objects.DataSetMetadata;

import java.io.InputStream;

/**
 * Represents a class able to serialize a data set content into JSON.
 */
public interface Serializer {

    /**
     * Process <code>rawContent</code> and returns a {@link java.io.InputStream} to the JSON output.
     * @param rawContent The data set content to process.
     * @param metadata Data set metadata (use it for column names).
     * @return A {@link java.io.InputStream} to the JSON transformation of the <code>rawContent</code>.
     */
    InputStream serialize(InputStream rawContent, DataSetMetadata metadata);
}
