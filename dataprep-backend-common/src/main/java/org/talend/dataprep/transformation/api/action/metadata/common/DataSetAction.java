package org.talend.dataprep.transformation.api.action.metadata.common;

import java.util.Map;

import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;

/**
 * Interface used to apply an action on a whole dataset.
 */
public interface DataSetAction {

    /**
     * Apply action on the whole dataset.
     *
     * @param row the dataset row.
     * @param context the transformation context.
     * @param parameters the action parameters.
     */
    void applyOnDataSet(final DataSetRow row, final TransformationContext context, final Map<String, String> parameters);
}
