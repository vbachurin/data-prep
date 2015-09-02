package org.talend.dataprep.api.dataset.location;

import org.talend.dataprep.api.dataset.DataSetLocation;

/**
 * Location used for local store.
 */
public class LocalStoreLocation implements DataSetLocation {

    /** Name of this store. */
    public static final String NAME = "local";

    /**
     * @see DataSetLocation#getLocationType()
     */
    @Override
    public String getLocationType() {
        return NAME;
    }

    /**
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object o) {

        // since there's no field to compare, the comparison is only performed on the class

        if (this == o) {
            return true;
        }
        return !(o == null || getClass() != o.getClass());
    }

}
