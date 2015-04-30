package org.talend.dataprep.exception;

import java.util.List;

/**
 * Interface used to unify error message behaviour.
 */
public interface ErrorCode {

    /** Internal server error. */
    int SC_500 =500;

    /** Bad request. */
    int SC_400 =400;


    /**
     * @return the product used for the error message... TDP ?
     */
    String getProduct();


    /**
     * @return the group this message belongs to (API, DATASET, PREPARATION...)
     */
    String getGroup();


    /**
     * @return the http status to return.
     */
    int getHttpStatus();

    /**
     * @return the expected context entries if any.
     */
    List<String> getExpectedContextEntries();

    /**
     * @return the full code for this message.
     */
    default String getCode() {
        return this.toString();
    }


}
