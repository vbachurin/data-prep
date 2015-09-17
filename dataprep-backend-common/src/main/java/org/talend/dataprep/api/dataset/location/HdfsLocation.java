package org.talend.dataprep.api.dataset.location;

import java.util.Objects;

import org.talend.dataprep.api.dataset.DataSetLocation;

/**
 * Location used for remote hdfs dataset.
 */
public class HdfsLocation extends AbstractUrlLocation implements DataSetLocation {

    /** Name of the http location. */
    public static final String NAME = "hdfs";

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
