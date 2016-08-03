package org.talend.dataprep.transformation.actions.common.new_actions_api;

import org.apache.commons.lang.ObjectUtils;
import org.talend.dataprep.api.dataset.DataSetRow;

import java.util.function.Predicate;

public final class ActionFilters {

    private ActionFilters() {
    }

    public static Predicate<DataSetRow> oneRow(long rowId) {
        return r -> ObjectUtils.equals(r.getTdpId(), rowId);
    }

}
