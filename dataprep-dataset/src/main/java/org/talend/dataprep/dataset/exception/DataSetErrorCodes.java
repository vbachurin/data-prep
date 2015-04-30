package org.talend.dataprep.dataset.exception;

import org.talend.dataprep.exception.ErrorCode;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Dataset error codes.
 */
public enum DataSetErrorCodes implements ErrorCode {
    UNEXPECTED_IO_EXCEPTION(500),
    UNABLE_TO_READ_DATASET_CONTENT(500),
    UNEXPECTED_JMS_EXCEPTION(500),
    UNABLE_TO_CLEAR_DATASETS(500),
    UNABLE_TO_DELETE_DATASET(400, "dataSetId"),
    UNABLE_TO_CONNECT_TO_HDFS(500, "location"),
    UNABLE_TO_STORE_DATASET_CONTENT(500, "id"),
    UNABLE_TO_ANALYZE_COLUMN_TYPES(500);

    /** The http status to use. */
    private int httpStatus;

    /** Expected entries to be in the context. */
    private List<String> expectedContextEntries;

    /**
     * default constructor.
     * 
     * @param httpStatus the http status to use.
     */
    DataSetErrorCodes(int httpStatus) {
        this.httpStatus = httpStatus;
        this.expectedContextEntries = Collections.emptyList();
    }

    /**
     * default constructor.
     *
     * @param httpStatus the http status to use.
     * @param contextEntries expected context entries.
     */
    DataSetErrorCodes(int httpStatus, String... contextEntries) {
        this.httpStatus = httpStatus;
        this.expectedContextEntries = Arrays.asList(contextEntries);
    }

    /**
     * @return the product.
     */
    @Override
    public String getProduct() {
        return "TDP"; //$NON-NLS-1$
    }

    /**
     * @return the group.
     */
    @Override
    public String getGroup() {
        return "DSS"; //$NON-NLS-1$
    }

    /**
     * @return the http status.
     */
    @Override
    public int getHttpStatus() {
        return httpStatus;
    }

    /**
     * @return the expected context entries.
     */
    public List<String> getExpectedContextEntries() {
        return expectedContextEntries;
    }
}
