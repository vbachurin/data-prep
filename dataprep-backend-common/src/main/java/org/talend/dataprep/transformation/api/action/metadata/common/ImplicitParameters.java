package org.talend.dataprep.transformation.api.action.metadata.common;

import static org.apache.commons.lang.StringUtils.EMPTY;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.talend.dataprep.transformation.api.action.parameters.Parameter;
import org.talend.dataprep.transformation.api.action.parameters.ParameterType;

/**
 * Common implicit parameters used by nearly all actions.
 */
public enum ImplicitParameters {

    COLUMN_ID("column_id", ParameterType.STRING, EMPTY),

    ROW_ID("row_id", ParameterType.STRING, EMPTY),

    SCOPE("scope", ParameterType.STRING, EMPTY),

    FILTER("filter", ParameterType.FILTER, EMPTY);

    /** The paramter. */
    private final Parameter parameter;

    /**
     * Constructor.
     *
     * @param key parameter key.
     * @param type type of parameter.
     * @param defaultValue the parameter default value.
     */
    ImplicitParameters(final String key, final ParameterType type, final String defaultValue) {
        this.parameter = new Parameter(key, type, defaultValue, true);
    }

    /**
     * @return the parameter key.
     */
    public String getKey() {
        return parameter.getName();
    }

    /**
     * @return the actual parameter.
     */
    public Parameter getParameter() {
        return parameter;
    }

    /**
     * @return the full list of implicit parameters.
     */
    public static List<Parameter> getParameters() {
        return Arrays.stream(values()) //
                .map(ImplicitParameters::getParameter) //
                .collect(Collectors.toList());
    }
}
