package org.talend.dataprep.transformation.exception;

import org.talend.dataprep.exception.ErrorCode;
/**
 * Transformation error codes.
 */
public enum TransformationErrorCodes implements ErrorCode {

    UNABLE_TO_PARSE_JSON(SC_400), //TODO what is the difference with CommonErrorCodes.UNABLE_TO_PARSE_JSON ?
    UNEXPECTED_EXCEPTION(SC_500),
    UNABLE_TO_COMPUTE_DATASET_ACTIONS(SC_500),
    UNABLE_TO_WRITE_JSON(SC_500);


    /** The http status to use. */
    private int httpStatus;


    /**
     * default constructor.
     * @param httpStatus the http status to use.
     */
    TransformationErrorCodes(int httpStatus) {
        this.httpStatus= httpStatus;
    }


    /**
     * @return the product.
     */
    @Override
    public String getGroup() {
        return "TS"; //$NON-NLS-1$
    }


    /**
     * @return the group.
     */
    @Override
    public String getProduct() {
        return "TDP"; //$NON-NLS-1$
    }


    /**
     * @return the http status.
     */
    @Override
    public int getHttpStatus() {
        return httpStatus;
    }
}
