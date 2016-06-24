// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// https://github.com/Talend/data-prep/blob/master/LICENSE
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package org.talend.dataprep.dataset.store.content;

import java.io.InputStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.talend.dataprep.api.dataset.DataSetContent;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.json.DataSetRowIterator;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.CommonErrorCodes;
import org.talend.dataprep.schema.FormatFamily;
import org.talend.dataprep.schema.Serializer;

/**
 * Base class for DataSet content stores.
 */
public abstract class DataSetContentStore {

    /** Format guesser factory. */
    @Autowired
    protected FormatFamily.Factory factory;

    /** DataPrep ready jackson builder. */
    @Autowired
    protected Jackson2ObjectMapperBuilder builder;

    /**
     * Stores (persists) a data set raw content to a storage. The only expectation is for {@link #get(DataSetMetadata)}
     * to return content after this method ends.
     *
     * @param dataSetMetadata The data set metadata attached to the {@link DataSetMetadata data set}.
     * @param dataSetContent Content of the data set.
     * @see #get(DataSetMetadata)
     * @see #delete(DataSetMetadata)
     */
    public abstract void storeAsRaw(DataSetMetadata dataSetMetadata, InputStream dataSetContent);

    /**
     * Returns the {@link DataSetMetadata data set} content as <b>JSON</b> format. Whether data set content was JSON or
     * not, method is expected to provide a JSON output. It's up to the implementation to:
     * <ul>
     * <li>Convert data content to JSON.</li>
     * <li>Throw an exception if data set is not ready for read (content type missing).</li>
     * </ul>
     * Implementations are also encouraged to implement method with no blocking code.
     *
     * @param dataSetMetadata The {@link DataSetMetadata data set} to read content from.
     * @return A valid <b>JSON</b> stream. It is a JSON array where each element in the array contains a single data set
     * row (it does not mean there's a line in input stream per data set row, a data set row might be split on multiple
     * rows in stream).
     */
    protected InputStream get(DataSetMetadata dataSetMetadata) {
        return get(dataSetMetadata, -1);
    }

    /**
     * Returns the {@link DataSetMetadata data set} content as <b>JSON</b> format. Whether data set content was JSON or
     * not, method is expected to provide a JSON output. It's up to the implementation to:
     * <ul>
     * <li>Convert data content to JSON.</li>
     * <li>Throw an exception if data set is not ready for read (content type missing).</li>
     * </ul>
     * Implementations are also encouraged to implement method with no blocking code.
     *
     * @param dataSetMetadata The {@link DataSetMetadata data set} to read content from.
     * @param limit A limit to pass to content supplier (use -1 for "no limit). Used as parameter for both raw content supplier
     * and JSON serializer.
     * @return A valid <b>JSON</b> stream. It is a JSON array where each element in the array contains a single data set
     * row (it does not mean there's a line in input stream per data set row, a data set row might be split on multiple
     * rows in stream).
     */
    protected InputStream get(DataSetMetadata dataSetMetadata, long limit) {
        DataSetContent content = dataSetMetadata.getContent();
        Serializer serializer = factory.getFormatFamily(content.getFormatFamilyId()).getSerializer();
        return serializer.serialize(getAsRaw(dataSetMetadata, limit), dataSetMetadata, limit);
    }

    /**
     * Similarly to {@link #get(DataSetMetadata)} returns the content of the data set but as a {@link Stream stream} of
     * {@link DataSetRow rows} instead of JSON content. Same as calling {@link #get(DataSetMetadata)} (DataSetMetadata, long)}
     * with limit = -1.
     *
     * @param dataSetMetadata The {@link DataSetMetadata data set} to read rows from.
     * @return A valid <b>{@link DataSetRow}</b> stream.
     */
    public Stream<DataSetRow> stream(DataSetMetadata dataSetMetadata) {
        return stream(dataSetMetadata, -1);
    }

    /**
     * Similarly to {@link #get(DataSetMetadata)} returns the content of the data set but as a {@link Stream stream} of
     * {@link DataSetRow rows} instead of JSON content.
     *
     * @param dataSetMetadata The {@link DataSetMetadata data set} to read rows from.
     * @param limit A limit to pass to raw content supplier (use -1 for "no limit). Used as parameter to call
     * {@link #get(DataSetMetadata, long)}.
     * @return A valid <b>{@link DataSetRow}</b> stream.
     */
    public Stream<DataSetRow> stream(DataSetMetadata dataSetMetadata, long limit) {
        final InputStream inputStream = get(dataSetMetadata, limit);
        final DataSetRowIterator iterator = new DataSetRowIterator(inputStream, true);
        final Iterable<DataSetRow> rowIterable = () -> iterator;
        Stream<DataSetRow> dataSetRowStream = StreamSupport.stream(rowIterable.spliterator(), false);
        // make sure to close the original input stream when closing this one
        dataSetRowStream = dataSetRowStream.onClose(() -> {
            try {
                inputStream.close();
            } catch (Exception e) {
                throw new TDPException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
            }
        });

        return dataSetRowStream;
    }

    /**
     * Returns the {@link DataSetMetadata data set} content as "raw" (i.e. the content supplied by user upon data set
     * creation). Same as calling {@link #getAsRaw(DataSetMetadata)} (DataSetMetadata, long)} with limit = -1.
     *
     * @param dataSetMetadata The {@link DataSetMetadata data set} to read content from.
     * @return The content associated with <code>dataSetMetadata</code>.
     */
    public InputStream getAsRaw(DataSetMetadata dataSetMetadata) {
        return getAsRaw(dataSetMetadata, -1);
    }

    /**
     * Returns the {@link DataSetMetadata data set} content as "raw" (i.e. the content supplied by user upon data set
     * creation).
     *
     * @param dataSetMetadata The {@link DataSetMetadata data set} to read content from.
     * @param limit A limit to pass to raw content supplier (use -1 for "no limit).
     * @return The content associated with <code>dataSetMetadata</code>.
     */
    public abstract InputStream getAsRaw(DataSetMetadata dataSetMetadata, long limit);

    /**
     * Deletes the {@link DataSetMetadata data set}. No recovery operation is expected.
     *
     * @param dataSetMetadata The data set to delete.
     */
    public abstract void delete(DataSetMetadata dataSetMetadata);

    /**
     * Removes all stored content. No recovery operation is expected.
     */
    public abstract void clear();
}
