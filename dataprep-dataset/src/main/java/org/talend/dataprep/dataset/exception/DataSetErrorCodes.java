package org.talend.dataprep.dataset.exception;

import org.talend.dataprep.exception.ErrorCode;

/**
 * Dataset error codes.
 */
public enum DataSetErrorCodes implements ErrorCode {
    UNEXPECTED_IO_EXCEPTION(SC_500),
    UNABLE_TO_READ_DATASET_CONTENT(SC_500),
    UNEXPECTED_JMS_EXCEPTION(SC_500),
    UNABLE_TO_CLEAR_DATASETS(SC_500),
    UNABLE_TO_DELETE_DATASET(SC_500),
    UNABLE_TO_CONNECT_TO_HDFS(SC_500),
    UNABLE_TO_STORE_DATASET_CONTENT(SC_500),
    UNABLE_TO_ANALYZE_COLUMN_TYPES(SC_500);


    /** The http status to use. */
    private int httpStatus;


    /**
     * default constructor.
     * @param httpStatus the http status to use.
     */
    DataSetErrorCodes(int httpStatus) {
        this.httpStatus= httpStatus;
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
}
