package org.talend.dataprep.preparation.store;

import org.talend.dataprep.api.DataSetMetadata;
import org.talend.dataprep.preparation.Preparation;

public interface PreparationRepository {

    /**
     * @return A {@link Iterable iterable} of {@link DataSetMetadata data set}.
     * Returned data set are expected to be visible by current user.
     */
    Iterable<Preparation> list();

    /**
     * <p>
     * Creates a new {@link DataSetMetadata data set}. If a previous one already exists, it will
     * be updated by this operation.
     * </p>
     * <p>
     * <b>However</b>, if a previous data set exists but the current user has no write rights on it, an exception should
     * be thrown.
     * </p>
     *
     * @param preparation The {@link DataSetMetadata data set} to create or update.
     */
    void add(Preparation preparation);

    /**
     * <p>
     * Removes all {@link DataSetMetadata data sets} in this repository. Repository does not
     * provide rollback operation for this, use it with care!
     * </p>
     * <p>
     * Please note this methods only removes data set the current user has write access on.
     * </p>
     */
    void clear();

    /**
     * @return The number of {@link DataSetMetadata data sets} the current user can see.
     */
    int size();

    /**
     * Returns the {@link DataSetMetadata data set} with given id.
     *
     * @param id A data set id.
     * @return The {@link DataSetMetadata} with given <code>id</code>.
     */
    Preparation get(String id);

    /**
     * Removes the {@link DataSetMetadata data set} with given id.
     *
     * @param id The id of the {@link DataSetMetadata data set} to delete.
     * @see DataSetMetadata#getId()
     */
    void remove(String id);
}
