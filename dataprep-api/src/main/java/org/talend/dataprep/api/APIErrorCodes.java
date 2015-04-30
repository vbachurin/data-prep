package org.talend.dataprep.api;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.talend.dataprep.exception.ErrorCode;

public enum APIErrorCodes implements ErrorCode {
    UNABLE_TO_DELETE_PREPARATION(SC_400),
    UNABLE_TO_CREATE_DATASET(SC_400),
    UNABLE_TO_CREATE_OR_UPDATE_DATASET(SC_400),
    UNABLE_TO_DELETE_DATASET(SC_400, "dataSetId", "preparations"),
    UNABLE_TO_RETRIEVE_DATASET_CONTENT(SC_400, "id"),
    UNABLE_TO_RETRIEVE_DATASET_METADATA(SC_400),
    UNABLE_TO_LIST_DATASETS(SC_400),
    UNABLE_TO_ACTIONS_TO_PREPARATION(SC_400, "id"),
    UNABLE_TO_CREATE_PREPARATION(SC_500), // can also be 400 ?
    UNABLE_TO_RETRIEVE_PREPARATION_LIST(SC_400),
    UNABLE_TO_RETRIEVE_PREPARATION_CONTENT(SC_400),
    UNABLE_TO_UPDATE_PREPARATION(SC_400, "id"),
    UNABLE_TO_RETRIEVE_SUGGESTED_ACTIONS(SC_400, "columnName", "dataSetId"),
    UNABLE_TO_FIND_COMMAND(SC_500, "class", "args"),
    UNABLE_TO_GET_PREPARATION_DETAILS(SC_400),
    UNABLE_TO_TRANSFORM_DATASET(SC_400, "dataSetId"),
    UNABLE_TO_UPDATE_ACTION_IN_PREPARATION(SC_400, "id");

    /** The http status to use. */
    private int httpStatus;

    /** Expected entries to be in the context. */
    private List<String> expectedContextEntries;

    /**
     * default constructor.
     * 
     * @param httpStatus the http status to use.
     */
    APIErrorCodes(int httpStatus) {
        this.httpStatus = httpStatus;
        this.expectedContextEntries = Collections.emptyList();
    }

    /**
     * default constructor.
     * 
     * @param httpStatus the http status to use.
     * @param contextEntries expected context entries.
     */
    APIErrorCodes(int httpStatus, String... contextEntries) {
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
        return "API"; //$NON-NLS-1$
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
