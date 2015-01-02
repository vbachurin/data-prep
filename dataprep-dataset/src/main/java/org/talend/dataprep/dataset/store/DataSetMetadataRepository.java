package org.talend.dataprep.dataset.store;

import org.talend.dataprep.dataset.objects.DataSetMetadata;

public interface DataSetMetadataRepository {

    /**
     * @return A {@link java.lang.Iterable iterable} of {@link org.talend.dataprep.dataset.objects.DataSetMetadata data set}.
     * Returned data set are expected to be visible by current user.
     */
    Iterable<DataSetMetadata> list();

    /**
     * <p>
     * Creates a new {@link org.talend.dataprep.dataset.objects.DataSetMetadata data set}. If a previous one already exists, it
     * will be updated by this operation.
     * </p>
     * <p>
     * <b>However</b>, if a previous data set exists but the current user has no write rights on it, an exception should
     * be thrown.
     * </p>
     * 
     * @param dataSetMetadata The {@link org.talend.dataprep.dataset.objects.DataSetMetadata data set} to create or update.
     */
    void add(DataSetMetadata dataSetMetadata);

    /**
     * <p>
     * Removes all {@link org.talend.dataprep.dataset.objects.DataSetMetadata data sets} in this repository. Repository does not
     * provide rollback operation for this, use it with care!
     * </p>
     * <p>
     * Please note this methods only removes data set the current user has write access on.
     * </p>
     */
    void clear();

    /**
     * @return The number of {@link org.talend.dataprep.dataset.objects.DataSetMetadata data sets} the current user can see.
     */
    int size();

    /**
     * Returns the {@link org.talend.dataprep.dataset.objects.DataSetMetadata data set} with given id.
     * 
     * @param id A data set id.
     * @return The {@link org.talend.dataprep.dataset.objects.DataSetMetadata} with given <code>id</code>.
     */
    DataSetMetadata get(String id);

    /**
     * Removes the {@link org.talend.dataprep.dataset.objects.DataSetMetadata data set} with given id.
     * 
     * @param id The id of the {@link org.talend.dataprep.dataset.objects.DataSetMetadata data set} to delete.
     * @see org.talend.dataprep.dataset.objects.DataSetMetadata#getId()
     */
    void remove(String id);
}
