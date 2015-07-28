package org.talend.dataprep.api.dataset.location;

import java.util.Objects;

import org.talend.dataprep.api.dataset.DataSetLocation;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Location used for remote hdfs dataset.
 */
public class HdfsLocation implements DataSetLocation {

    /** Name of the http location. */
    public static final String NAME = "hdfs";

    /** The dataset http url. */
    @JsonProperty("url")
    private String url;

    /**
     * @see DataSetLocation#getLocationType()
     */
    @Override
    public String getLocationType() {
        return NAME;
    }

    /**
     * @return the Url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set.
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return "HdfsLocation{" + "url='" + url + '\'' + '}';
    }

    /**
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        HdfsLocation that = (HdfsLocation) o;
        return Objects.equals(url, that.url);
    }

    /**
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(url);
    }
}
