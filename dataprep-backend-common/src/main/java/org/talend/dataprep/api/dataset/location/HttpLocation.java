package org.talend.dataprep.api.dataset.location;

import java.util.Objects;

import org.talend.dataprep.api.dataset.DataSetLocation;

/**
 * Location used for remote http dataset.
 */
public class HttpLocation extends AbstractUrlLocation implements DataSetLocation {

    /** Name of the http location. */
    public static final String NAME = "http";

    /**
     * @see DataSetLocation#getLocationType()
     */
    @Override
    public String getLocationType() {
        return NAME;
    }

    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return "HttpLocation{" + "url='" + url + '\'' + '}';
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
        HttpLocation that = (HttpLocation) o;
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
