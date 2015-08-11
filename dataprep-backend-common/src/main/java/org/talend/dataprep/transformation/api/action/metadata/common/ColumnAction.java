package org.talend.dataprep.transformation.api.action.metadata.common;

import java.util.Map;

import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;

/**
 * Interface used to apply an action on a column.
 */
public interface ColumnAction {

    /**
     * Apply action on a column.
     *
     * @param row the dataset row.
     * @param context the transformation context.
     * @param parameters the action parameters.
     */
    void applyOnColumn(final DataSetRow row, final TransformationContext context, final Map<String, String> parameters, final String columnId);
}
