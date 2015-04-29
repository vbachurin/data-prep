package org.talend.dataprep.exception;

public enum CommonErrorCodes implements ErrorCode {
    UNEXPECTED_EXCEPTION(SC_500),
    UNABLE_TO_PARSE_JSON(SC_500),
    UNABLE_TO_SERIALIZE_TO_JSON(SC_500),
    UNABLE_TO_COMPUTE_ID(SC_500),
    UNABLE_TO_PRINT_PREPARATION(SC_500),
    UNABLE_TO_READ_CONTENT(SC_500),
    UNABLE_TO_PARSE_ACTIONS(SC_500);

    /** The http status to use. */
    private int httpStatus;

    /**
     * default constructor.
     * @param httpStatus the http status to use.
     */
    CommonErrorCodes(int httpStatus) {
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
        return "ALL"; //$NON-NLS-1$
    }

    /**
     * @return the http status.
     */
    @Override
    public int getHttpStatus() {
        return httpStatus;
    }
}
