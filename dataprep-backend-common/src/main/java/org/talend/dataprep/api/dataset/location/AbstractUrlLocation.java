package org.talend.dataprep.api.dataset.location;

import java.io.Serializable;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Base class for all Url related dataset location.
 */
public abstract class AbstractUrlLocation implements Serializable {

    /** The dataset http url. */
    @JsonProperty("url")
    protected String url;

    /**
     * @return the Url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Encode and set the given url.
     * @param url the url to set.
     * @throws URIException if the given url is not valid.
     */
    public void setUrl(String url) throws URIException {
        if (StringUtils.isNotBlank(url)) {
            this.url = URIUtil.encodeQuery(url);
        }
        else {
            this.url = url;
        }
    }
}
