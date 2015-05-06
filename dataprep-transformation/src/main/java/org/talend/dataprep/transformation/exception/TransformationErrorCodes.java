package org.talend.dataprep.transformation.exception;

import org.talend.dataprep.exception.ErrorCode;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Transformation error codes.
 */
public enum TransformationErrorCodes implements ErrorCode {

    UNABLE_TO_PARSE_JSON(400), // TODO what is the difference with CommonErrorCodes.UNABLE_TO_PARSE_JSON ?
    UNEXPECTED_EXCEPTION(500),
    UNABLE_TO_COMPUTE_DATASET_ACTIONS(500),
    UNABLE_TO_WRITE_JSON(500);

    /** The http status to use. */
    private int httpStatus;

    /** Expected entries to be in the context. */
    private List<String> expectedContextEntries;

    /**
     * default constructor.
     * 
     * @param httpStatus the http status to use.
     */
    TransformationErrorCodes(int httpStatus) {
        this.httpStatus = httpStatus;
        this.expectedContextEntries = Collections.emptyList();
    }

    /**
     * default constructor.
     *
     * @param httpStatus the http status to use.
     * @param contextEntries expected context entries.
     */
    TransformationErrorCodes(int httpStatus, String... contextEntries) {
        this.httpStatus = httpStatus;
        this.expectedContextEntries = Arrays.asList(contextEntries);
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

    /**
     * @return the expected context entries.
     */
    public List<String> getExpectedContextEntries() {
        return expectedContextEntries;
    }
}
