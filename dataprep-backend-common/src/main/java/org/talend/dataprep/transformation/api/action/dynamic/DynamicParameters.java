package org.talend.dataprep.transformation.api.action.dynamic;

import org.talend.dataprep.api.dataset.DataSet;
import org.talend.dataprep.transformation.api.action.parameters.GenericParameter;

public interface DynamicParameters {

    GenericParameter getParameters(String columnId, DataSet dataSet);
}
