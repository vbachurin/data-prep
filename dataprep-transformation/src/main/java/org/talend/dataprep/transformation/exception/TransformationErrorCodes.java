package org.talend.dataprep.transformation.exception;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.talend.dataprep.exception.ErrorCode;

import static org.springframework.http.HttpStatus.*;

/**
 * Transformation error codes.
 */
public enum TransformationErrorCodes implements ErrorCode {
    //400
    UNABLE_TO_PARSE_JSON(BAD_REQUEST), // TODO what is the difference with CommonErrorCodes.UNABLE_TO_PARSE_JSON ?

    //404
    UNKNOWN_DYNAMIC_ACTION(NOT_FOUND, "value"),

    //415
    OUTPUT_TYPE_NOT_SUPPORTED(UNSUPPORTED_MEDIA_TYPE),

    //500
    UNABLE_TO_COMPUTE_DATASET_ACTIONS(INTERNAL_SERVER_ERROR),
    UNABLE_TO_WRITE_JSON(INTERNAL_SERVER_ERROR),
    UNABLE_TRANSFORM_DATASET(INTERNAL_SERVER_ERROR),
    UNEXPECTED_EXCEPTION(INTERNAL_SERVER_ERROR);

    /** The http status to use. */
    private int httpStatus;

    /** Expected entries to be in the context. */
    private List<String> expectedContextEntries;

    /**
     * default constructor.
     * 
     * @param httpStatus the http status to use.
     */
    TransformationErrorCodes(final HttpStatus httpStatus) {
        this.httpStatus = httpStatus.value();
        this.expectedContextEntries = Collections.emptyList();
    }

    /**
     * default constructor.
     *
     * @param httpStatus the http status to use.
     * @param contextEntries expected context entries.
     */
    TransformationErrorCodes(final HttpStatus httpStatus, final String... contextEntries) {
        this.httpStatus = httpStatus.value();
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
    public Collection<String> getExpectedContextEntries() {
        return expectedContextEntries;
    }
}
