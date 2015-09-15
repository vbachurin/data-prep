package org.talend.dataprep.transformation.api.action.dynamic;

/**
 * Representation of a "Generic" Parameter.
 */
public class GenericParameter {

    /** Parameter type (item, parameter, cluster, ...). */
    final String type;

    /** Parameter details. this can be an array, a map, an object. */
    final Object details;

    /**
     * Default constructor.
     *
     * @param type the parameter type.
     * @param details the parameter details.
     */
    public GenericParameter(final String type, final Object details) {
        this.type = type;
        this.details = details;
    }

    /**
     * @return the parameter type.
     */
    public String getType() {
        return type;
    }

    /**
     * @return the parameter details.
     */
    public Object getDetails() {
        return details;
    }
}
