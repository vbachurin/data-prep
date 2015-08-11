package org.talend.dataprep.transformation.api.action.metadata.common;

import java.util.Map;

import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;

public interface IColumnAction {
    void applyOnColumn(final DataSetRow row, final TransformationContext context, final Map<String, String> parameters, final String columnId);
}
