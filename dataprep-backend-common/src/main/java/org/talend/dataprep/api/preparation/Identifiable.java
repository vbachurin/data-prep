package org.talend.dataprep.api.preparation;

import org.springframework.data.annotation.AccessType;

public abstract class Identifiable {

    // Only there for MongoDB serialization purposes
    @AccessType(AccessType.Type.PROPERTY)
    protected String id;

    public abstract String id();

    // Only there for MongoDB serialization purposes
    public abstract String getId();

    // Only there for MongoDB serialization purposes
    public abstract void setId(String id);
}
