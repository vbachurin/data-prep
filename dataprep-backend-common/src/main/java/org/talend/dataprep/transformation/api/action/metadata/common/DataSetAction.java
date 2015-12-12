package org.talend.dataprep.transformation.api.action.metadata.common;

import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.transformation.api.action.context.ActionContext;

/**
 * Interface used to apply an action on a whole dataset.
 */
public interface DataSetAction {

    /**
     * Apply action on the whole dataset.
     * @param row the dataset row.
     * @param context the transformation context.
     */
    void applyOnDataSet(final DataSetRow row, final ActionContext context);
}
