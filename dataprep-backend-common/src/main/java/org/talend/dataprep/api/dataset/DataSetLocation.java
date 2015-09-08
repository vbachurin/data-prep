package org.talend.dataprep.api.dataset;

import java.io.Serializable;

import org.talend.dataprep.api.dataset.location.HdfsLocation;
import org.talend.dataprep.api.dataset.location.HttpLocation;
import org.talend.dataprep.api.dataset.location.LocalStoreLocation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Information about the dataset location.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ //
@JsonSubTypes.Type(value = LocalStoreLocation.class, name = LocalStoreLocation.NAME), //
        @JsonSubTypes.Type(value = HttpLocation.class, name = HttpLocation.NAME), //
        @JsonSubTypes.Type(value = HdfsLocation.class, name = HdfsLocation.NAME) })
public interface DataSetLocation extends Serializable {

    /** Return the location name (e.g local, http...). */
    @JsonIgnore
    // ignored not to duplicate with the @JsonTypeInfo defined in the class
    String getLocationType();

}
