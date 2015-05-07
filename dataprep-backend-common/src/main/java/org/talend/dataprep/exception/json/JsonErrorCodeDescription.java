package org.talend.dataprep.exception.json;

import java.util.Collection;
import java.util.List;

import org.talend.dataprep.exception.ErrorCode;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class used to ease an ErrorCode description json manipulation.
 */
public class JsonErrorCodeDescription implements ErrorCode {

    /** The error code product. */
    private String product;

    /** The error code group. */
    private String group;

    /** The error code... Code ! :-) */
    private String code;

    /** The error code http status. */
    @JsonProperty("http-status-code")
    private int httpStatus;

    /** The error code context. */
    @JsonProperty("context")
    private Collection<String> expectedContextEntries;

    /**
     * Default empty constructor needed for json parsing.
     */
    public JsonErrorCodeDescription() {
    }

    /**
     * Copy constructor.
     * 
     * @param copy the error code to copy.
     */
    public JsonErrorCodeDescription(ErrorCode copy) {
        this.product = copy.getProduct();
        this.group = copy.getGroup();
        this.code = copy.getCode();
        this.httpStatus = copy.getHttpStatus();
        this.expectedContextEntries = copy.getExpectedContextEntries();
    }

    /**
     * @return the error code product.
     */
    @Override
    public String getProduct() {
        return this.product;
    }

    /**
     * @param product the product to set.
     */
    public void setProduct(String product) {
        this.product = product;
    }

    /**
     * @return the error code group.
     */
    @Override
    public String getGroup() {
        return this.group;
    }

    /**
     * @param group the error code group to set.
     */
    public void setGroup(String group) {
        this.group = group;
    }

    /**
     * @return the error code... code ! :-)
     */
    @Override
    public String getCode() {
        return this.code;
    }

    /**
     * @param code the error code to set.
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the error code http status.
     */
    @Override
    public int getHttpStatus() {
        return this.httpStatus;
    }

    /**
     * @param httpStatus the error code http status to set.
     */
    public void setHttpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
    }

    /**
     * @return the error code expected context entries.
     */
    @Override
    public Collection<String> getExpectedContextEntries() {
        return this.expectedContextEntries;
    }

    /**
     * @param expectedContextEntries the error code expected context entries to set.
     */
    public void setContext(List<String> expectedContextEntries) {
        this.expectedContextEntries = expectedContextEntries;
    }

}
