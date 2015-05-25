package org.talend.dataprep.transformation.api.action.parameters;

/**
 * Representation of a "Generic" Parameter.
 */
public class GenericParameter {
    /**
     * Parameter type (item, parameter, cluster, ...)
     */
    final String type;
    /**
     * Parameter details. this can be an array, a map, an object
     */
    final Object details;

    public GenericParameter(final String type, final Object details) {
        this.type = type;
        this.details = details;
    }

    public String getType() {
        return type;
    }

    public Object getDetails() {
        return details;
    }
}
