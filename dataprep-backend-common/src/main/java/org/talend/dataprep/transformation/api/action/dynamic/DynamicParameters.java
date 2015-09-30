package org.talend.dataprep.transformation.api.action.dynamic;

import org.talend.dataprep.api.dataset.DataSet;

public interface DynamicParameters {

    GenericParameter getParameters(String columnId, DataSet dataSet);
}
