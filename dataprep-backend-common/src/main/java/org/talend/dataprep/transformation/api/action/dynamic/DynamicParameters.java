package org.talend.dataprep.transformation.api.action.dynamic;

import java.io.InputStream;

import org.talend.dataprep.transformation.api.action.parameters.GenericParameter;

public interface DynamicParameters {
    GenericParameter getParameters(String columnId, InputStream args);
}
