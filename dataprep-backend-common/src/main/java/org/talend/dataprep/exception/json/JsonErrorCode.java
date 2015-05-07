package org.talend.dataprep.exception.json;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.talend.dataprep.exception.ErrorCode;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class used to ease ErrorCode json manipulation.
 */
public class JsonErrorCode implements ErrorCode {

    @JsonProperty("code")
    private String code;

    /** The error code context. */
    @JsonProperty("context")
    private Map<String, Object> context;

    private int httpStatus;

    /**
     * Default empty constructor needed for json parsing.
     */
    public JsonErrorCode() {
    }

    /**
     * @return the error code product.
     */
    @Override
    public String getProduct() {
        return StringUtils.substringBefore(code, "_"); //$NON-NLS-1$
    }

    /**
     * @param product the product to set.
     */
    public void setProduct(String product) {
        // Nothing to do.
    }

    /**
     * @return the error code group.
     */
    @Override
    public String getGroup() {
        return StringUtils.substringBefore(StringUtils.substringAfter(code, "_"), "_"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * @param group the error code group to set.
     */
    public void setGroup(String group) {
        // Nothing to do.
    }

    /**
     * @return the error code... code ! :-)
     */
    @Override
    public String getCode() {
        return code.substring((getProduct() + '_' + getGroup()).length() + 1);
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
     * @return the error code context entries.
     */
    public Map<String, Object> getContext() {
        return this.context;
    }

    /**
     * @param context the error code context entries.
     */
    public void setContext(Map<String, Object> context) {
        this.context = context;
    }

    @Override
    public Collection<String> getExpectedContextEntries() {
        return context.keySet();
    }
}
