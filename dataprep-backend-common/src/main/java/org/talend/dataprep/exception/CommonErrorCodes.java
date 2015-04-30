package org.talend.dataprep.exception;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum CommonErrorCodes implements ErrorCode {
    UNEXPECTED_EXCEPTION(SC_500),
    UNABLE_TO_PARSE_JSON(SC_500),
    UNABLE_TO_SERIALIZE_TO_JSON(SC_500),
    UNABLE_TO_COMPUTE_ID(SC_500),
    UNABLE_TO_PRINT_PREPARATION(SC_500),
    UNABLE_TO_READ_CONTENT(SC_500),
    UNABLE_TO_PARSE_ACTIONS(SC_500),
    UNABLE_TO_PARSE_REQUEST(SC_400);        // e.g IllegalArgumentException

    /** The http status to use. */
    private int httpStatus;
    /** Expected entries to be in the context. */
    private List<String> expectedContextEntries;

    /**
     * default constructor.
     * @param httpStatus the http status to use.
     */
    CommonErrorCodes(int httpStatus) {
        this.httpStatus= httpStatus;
        this.expectedContextEntries = Collections.emptyList();
    }

    /**
     * default constructor.
     * 
     * @param httpStatus the http status to use.
     * @param contextEntries expected context entries.
     */
    CommonErrorCodes(int httpStatus, String... contextEntries) {
        this.httpStatus= httpStatus;
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
        return "ALL"; //$NON-NLS-1$
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
