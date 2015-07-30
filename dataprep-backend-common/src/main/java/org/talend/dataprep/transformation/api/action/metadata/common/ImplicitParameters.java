package org.talend.dataprep.transformation.api.action.metadata.common;

import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.talend.dataprep.api.type.Type.STRING;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.parameters.Parameter;

public enum ImplicitParameters {
    COLUMN_ID("column_id", STRING, EMPTY),
    ROW_ID("row_id", STRING, EMPTY),
    SCOPE("scope", STRING, EMPTY);

    private final Parameter parameter;

    ImplicitParameters(final String key, final Type type, final String defaultValue) {
        this.parameter = new Parameter(key, type.getName(), defaultValue, true);
    }

    public String getKey() {
        return parameter.getName();
    }

    public Parameter getParameter() {
        return parameter;
    }

    public static List<Parameter> getParameters() {
        return Arrays.stream(values()) //
                .map(ImplicitParameters::getParameter) //
                .collect(Collectors.toList());
    }
}
