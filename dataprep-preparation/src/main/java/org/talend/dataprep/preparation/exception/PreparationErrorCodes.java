package org.talend.dataprep.preparation.exception;

import org.talend.dataprep.exception.ErrorCode;

/**
 * Preparation error codes.
 */
public enum PreparationErrorCodes implements ErrorCode {
    PREPARATION_DOES_NOT_EXIST(SC_400), // TODO should be 404 ?
    UNABLE_TO_SERVE_PREPARATION_CONTENT(SC_400); //400


    /** The http status to use. */
    private int httpStatus;


    /**
     * default constructor.
     * @param httpStatus the http status to use.
     */
    PreparationErrorCodes(int httpStatus) {
        this.httpStatus= httpStatus;
    }

    /**
     * @return the product.
     */
    @Override
    public String getProduct() {
        return "TDP"; //$NON-NLS-1$
    }

    @Override
    public String getGroup() {
        return "PS"; //$NON-NLS-1$
    }

    /**
     * @return the http status.
     */
    @Override
    public int getHttpStatus() {
        return httpStatus;
    }
}
