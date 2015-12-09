package org.talend.dataprep.transformation.api.action.metadata.common;

import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.transformation.api.action.context.ActionContext;

/**
 * Interface used to apply an action on a cell.
 */
public interface CellAction {

    /**
     * Apply action on a cell.
     * @param row the dataset row.
     * @param context the transformation context.
     */
    void applyOnCell(final DataSetRow row, final ActionContext context);
}
