package org.talend.dataprep.preparation.exception;

import org.talend.dataprep.exception.ErrorCode;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Preparation error codes.
 */
public enum PreparationErrorCodes implements ErrorCode {
    PREPARATION_DOES_NOT_EXIST(400, "id"), // TODO should be 404 ?
    UNABLE_TO_SERVE_PREPARATION_CONTENT(400, "id", "version"); // 400

    /** The http status to use. */
    private int httpStatus;

    /** Expected entries to be in the context. */
    private List<String> expectedContextEntries;

    /**
     * default constructor.
     * 
     * @param httpStatus the http status to use.
     */
    PreparationErrorCodes(int httpStatus) {
        this.httpStatus = httpStatus;
        this.expectedContextEntries = Collections.emptyList();
    }

    /**
     * default constructor.
     *
     * @param httpStatus the http status to use.
     * @param contextEntries expected context entries.
     */
    PreparationErrorCodes(int httpStatus, String... contextEntries) {
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

    /**
     * @return the expected context entries.
     */
    public List<String> getExpectedContextEntries() {
        return expectedContextEntries;
    }
}
