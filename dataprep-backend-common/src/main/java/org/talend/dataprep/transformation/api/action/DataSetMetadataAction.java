package org.talend.dataprep.transformation.api.action;

import java.util.function.BiConsumer;

import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;

@FunctionalInterface
public interface DataSetMetadataAction extends BiConsumer<RowMetadata, TransformationContext> {
}
