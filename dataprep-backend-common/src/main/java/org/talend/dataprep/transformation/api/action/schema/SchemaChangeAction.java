package org.talend.dataprep.transformation.api.action.schema;

import java.util.HashSet;
import java.util.Set;

import org.talend.dataprep.transformation.api.action.context.TransformationContext;
import org.talend.dataprep.transformation.api.action.metadata.common.ColumnAction;

/**
 * This interface is a just marker to know which action are modifying the dataset schema (i.e column type/definition
 * etc..) Everything which touch the dataset column model
 */
public interface SchemaChangeAction extends ColumnAction {

    // Transformation Context key which contains a Set with column ids with type/domain forced
    String FORCED_TYPE_SET_CTX_KEY = SchemaChangeAction.class.getName() + "#columnForcedCtxKey";

    default Set<String> getForcedColumns(TransformationContext transformationContext) {
        Set<String> forcedColumns = (Set<String>) transformationContext.get(FORCED_TYPE_SET_CTX_KEY);
        if (forcedColumns == null) {
            forcedColumns = new HashSet<>();
            transformationContext.put(FORCED_TYPE_SET_CTX_KEY, forcedColumns);
        }
        return forcedColumns;
    }

    default void forceColumn(TransformationContext transformationContext, String columnId) {
        Set<String> forceColumns = getForcedColumns(transformationContext);
        forceColumns.add(columnId);

    }
}
