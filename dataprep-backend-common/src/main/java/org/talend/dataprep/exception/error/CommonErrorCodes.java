package org.talend.dataprep.exception.error;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public enum CommonErrorCodes implements ErrorCode {
    UNEXPECTED_EXCEPTION(500),
    UNABLE_TO_PARSE_JSON(500),
    UNABLE_TO_WRITE_JSON(500),
    UNABLE_TO_SERIALIZE_TO_JSON(500),
    UNABLE_TO_COMPUTE_ID(500),
    UNABLE_TO_PRINT_PREPARATION(500),
    UNABLE_TO_READ_CONTENT(500),
    UNABLE_TO_PARSE_ACTIONS(500),
    UNABLE_TO_PARSE_REQUEST(400), // e.g IllegalArgumentException
    UNABLE_TO_CONNECT_TO_HDFS(500, "location");

    /** The http status to use. */
    private int httpStatus;

    /** Expected entries to be in the context. */
    private List<String> expectedContextEntries;

    /**
     * default constructor.
     * 
     * @param httpStatus the http status to use.
     */
    CommonErrorCodes(int httpStatus) {
        this.httpStatus = httpStatus;
        this.expectedContextEntries = Collections.emptyList();
    }

    /**
     * default constructor.
     *
     * @param httpStatus the http status to use.
     */
    CommonErrorCodes(int httpStatus, String... contextEntries) {
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
    @Override
    public Collection<String> getExpectedContextEntries() {
        return expectedContextEntries;
    }
}
