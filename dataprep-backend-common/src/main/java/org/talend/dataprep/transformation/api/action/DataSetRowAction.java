package org.talend.dataprep.transformation.api.action;

import java.util.function.BiFunction;

import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.transformation.api.action.context.ActionContext;

@FunctionalInterface
public interface DataSetRowAction extends BiFunction<DataSetRow, ActionContext, DataSetRow> {
}
