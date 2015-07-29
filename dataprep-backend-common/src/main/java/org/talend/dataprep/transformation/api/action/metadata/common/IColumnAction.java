package org.talend.dataprep.transformation.api.action.metadata.common;

import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;

import java.util.Map;

public interface IColumnAction {
    void applyOnColumn(final DataSetRow row, final TransformationContext context, final Map<String, String> parameters, final String columnId);
}
