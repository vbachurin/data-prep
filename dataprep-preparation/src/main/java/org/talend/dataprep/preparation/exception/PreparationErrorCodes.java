package org.talend.dataprep.preparation.exception;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.talend.dataprep.exception.ErrorCode;

/**
 * Preparation error codes.
 */
public enum PreparationErrorCodes implements ErrorCode {
    PREPARATION_DOES_NOT_EXIST(400, "id"),
    PREPARATION_STEP_DOES_NOT_EXIST(400, "id", "stepId"),
    PREPARATION_ROOT_STEP_CANNOT_BE_CHANGED(400),
    UNABLE_TO_SERVE_PREPARATION_CONTENT(400, "id", "version");

    /** The http status to use. */
    private int httpStatus;

    /** Expected entries to be in the context. */
    private List<String> expectedContextEntries;

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
    @Override
    public Collection<String> getExpectedContextEntries() {
        return expectedContextEntries;
    }
}
