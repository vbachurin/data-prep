package org.talend.dataprep.exception;

/**
 * Interface used to unify error message behaviour.
 */
public interface ErrorCode {

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
     * @return the full code for this message.
     */
    default String getCode() {
        return this.toString();
    }
}
