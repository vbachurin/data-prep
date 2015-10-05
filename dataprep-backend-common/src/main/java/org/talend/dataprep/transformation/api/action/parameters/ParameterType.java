package org.talend.dataprep.transformation.api.action.parameters;

/**
 * List of available parameter type.
 */
public enum ParameterType {
                           STRING,
                           INTEGER,
                           SELECT,
                           COLUMN;

    public String asString() {
        return this.name().toLowerCase();
    }

}
