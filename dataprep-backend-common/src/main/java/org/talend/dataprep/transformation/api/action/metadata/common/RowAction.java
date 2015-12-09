package org.talend.dataprep.transformation.api.action.metadata.common;

import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.transformation.api.action.context.ActionContext;

/**
 * Interface used to apply action on a row.
 */
public interface RowAction {

    /**
     * Apply action on a row.
     * @param row        the dataset row.
     * @param context    the transformation context.
     */
    void applyOnLine(final DataSetRow row, final ActionContext context);
}
