package org.talend.dataprep.transformation.api.action.metadata.common;

import java.util.Map;

import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;

/**
 * Interface used to apply an action on a cell.
 */
public interface CellAction {

    /**
     * Apply action on a cell.
     *
     * @param row the dataset row.
     * @param context the transformation context.
     * @param parameters the action parameters.
     * @param rowId the row id.
     * @param columnId the column id.
     */
    void applyOnCell(final DataSetRow row, final TransformationContext context, final Map<String, String> parameters, final Long rowId, final String columnId);
}
