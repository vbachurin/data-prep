package org.talend.dataprep.transformation.api.action;

import java.util.function.BiConsumer;

import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;

@FunctionalInterface
public interface DataSetRowAction extends BiConsumer<DataSetRow, TransformationContext> {
}
