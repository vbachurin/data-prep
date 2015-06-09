package org.talend.dataprep.transformation.api.action.context;

import java.util.Map;

import org.talend.dataprep.api.dataset.RowMetadata;

/**
 * Transformation context used by ActionMetadata to store/access contextual values while running.
 *
 * The purpose of this class is to have a small memory footprint and not store the whole dataset. To prevent misuse of
 * this class in future / open developments, it's final.
 *
 * @see org.talend.dataprep.transformation.api.action.metadata.ActionMetadata#create(Map)
 */
public final class TransformationContext {

    /** The row metadata. */
    private RowMetadata transformedRowMetadata;

    /**
     * @param transformedRowMetadata the row metadata to build this context from.
     */
    public void setTransformedRowMetadata(RowMetadata transformedRowMetadata) {
        this.transformedRowMetadata = transformedRowMetadata;
    }

    /**
     * @return immutable row metadata.
     */
    public RowMetadata getTransformedRowMetadata() {
        if (transformedRowMetadata == null) {
            throw new IllegalStateException("transformed row metadata was not set. Did ColumnsTypeTransformer set it ?");
        }
        return transformedRowMetadata;
    }
}
