package org.talend.dataprep.dataset.store;

import java.io.InputStream;

import org.talend.dataprep.api.dataset.DataSetMetadata;

public interface DataSetContentStore {

    /**
     * Stores (persists) a data set content to a storage. The data set content must be a data set JSON format.
     *
     * @param dataSetMetadata The data set metadata attached to the {@link DataSetMetadata data
     * set}.
     * @param dataSetJsonContent Content of the data set (as JSON format, cannot use raw format as in
     * {@link #storeAsRaw(DataSetMetadata, InputStream)}.
     * @param actions The actions that created this new version of the dataset content. It cannot be <code>null</code>.
     * @see #get(DataSetMetadata)
     * @see #delete(DataSetMetadata)
     */
    void store(DataSetMetadata dataSetMetadata, InputStream dataSetJsonContent, String actions);

    /**
     * Stores (persists) a data set raw content to a storage. The only expectation is for
     * {@link #get(DataSetMetadata)} to return content after this method ends.
     *
     * @param dataSetMetadata The data set metadata attached to the {@link DataSetMetadata data
     * set}.
     * @param dataSetContent Content of the data set.
     * @see #get(DataSetMetadata)
     * @see #delete(DataSetMetadata)
     */
    void storeAsRaw(DataSetMetadata dataSetMetadata, InputStream dataSetContent);

    /**
     * Returns the {@link DataSetMetadata data set} content as <b>JSON</b> format. Whether data
     * set content was JSON or not, method is expected to provide a JSON output. It's up to the implementation to:
     * <ul>
     * <li>Convert data content to JSON.</li>
     * <li>Throw an exception if data set is not ready for read (content type missing).</li>
     * </ul>
     * Implementations are also encouraged to implement method with no blocking code.
     * 
     * @param dataSetMetadata The {@link DataSetMetadata data set} to read content from.
     * @return A valid <b>JSON</b> stream.
     */
    InputStream get(DataSetMetadata dataSetMetadata);

    /**
     * Returns the {@link DataSetMetadata data set} content as "raw" (i.e. the content supplied
     * by user upon data set creation).
     * 
     * @param dataSetMetadata The {@link DataSetMetadata data set} to read content from.
     * @return The content associated with <code>dataSetMetadata</code>.
     */
    InputStream getAsRaw(DataSetMetadata dataSetMetadata);

    /**
     * Deletes the {@link DataSetMetadata data set}. No recovery operation is expected.
     * 
     * @param dataSetMetadata The data set to delete.
     */
    void delete(DataSetMetadata dataSetMetadata);

    /**
     * Removes all stored content. No recovery operation is expected.
     */
    void clear();
}
