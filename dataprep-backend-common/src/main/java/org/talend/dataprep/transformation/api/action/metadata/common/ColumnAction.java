package org.talend.dataprep.transformation.api.action.metadata.common;

import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.transformation.api.action.context.ActionContext;

/**
 * Interface used to apply an action on a column.
 */
public interface ColumnAction {

    /**
     * Apply action on a column.
     * @param row the dataset row.
     * @param context the transformation context.
     */
    void applyOnColumn(final DataSetRow row, final ActionContext context);
}
