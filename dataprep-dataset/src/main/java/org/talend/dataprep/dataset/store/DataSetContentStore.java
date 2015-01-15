package org.talend.dataprep.dataset.store;

import org.talend.dataprep.dataset.objects.DataSetMetadata;

import java.io.InputStream;

public interface DataSetContentStore {

    /**
     * Stores (persists) a data set content to a storage. The only expectation is for
     * {@link #get(org.talend.dataprep.dataset.objects.DataSetMetadata)} to return content after this method ends.
     * 
     * @param dataSetMetadata The data set metadata attached to the
     * {@link org.talend.dataprep.dataset.objects.DataSetMetadata data set} .
     * @param dataSetContent Content of the data set.
     * @see #get(org.talend.dataprep.dataset.objects.DataSetMetadata)
     * @see #delete(org.talend.dataprep.dataset.objects.DataSetMetadata)
     */
    void store(DataSetMetadata dataSetMetadata, InputStream dataSetContent);

    /**
     * Returns the {@link org.talend.dataprep.dataset.objects.DataSetMetadata data set} content as <b>JSON</b> format.
     * Whether data set content was JSON or not, method is expected to provide a JSON output. It's up to the
     * implementation to:
     * <ul>
     * <li>Convert data content to JSON.</li>
     * <li>Throw an exception if data set is not ready for read (content type missing).</li>
     * </ul>
     * Implementations are also encouraged to implement method with no blocking code.
     * 
     * @param dataSetMetadata The {@link org.talend.dataprep.dataset.objects.DataSetMetadata data set} to read content
     * from.
     * @return A valid <b>JSON</b> stream.
     */
    InputStream get(DataSetMetadata dataSetMetadata);

    /**
     * Returns the {@link org.talend.dataprep.dataset.objects.DataSetMetadata data set} content as "raw" (i.e. the
     * content supplied by user upon data set creation).
     * 
     * @param dataSetMetadata The {@link org.talend.dataprep.dataset.objects.DataSetMetadata data set} to read content
     * from.
     * @return The content associated with <code>dataSetMetadata</code>.
     */
    InputStream getAsRaw(DataSetMetadata dataSetMetadata);

    /**
     * Deletes the {@link org.talend.dataprep.dataset.objects.DataSetMetadata data set}. No recovery operation is
     * expected.
     * 
     * @param dataSetMetadata The data set to delete.
     */
    void delete(DataSetMetadata dataSetMetadata);

    /**
     * Removes all stored content. No recovery operation is expected.
     */
    void clear();
}
