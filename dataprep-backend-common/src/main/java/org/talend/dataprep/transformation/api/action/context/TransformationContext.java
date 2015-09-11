package org.talend.dataprep.transformation.api.action.context;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;

/**
 * Transformation context used by ActionMetadata to store/access contextual values while running.
 *
 * The purpose of this class is to have a small memory footprint and not store the whole dataset. To prevent misuse of
 * this class in future / open developments, it's final.
 *
 * @see ActionMetadata#create(Map)
 */
public final class TransformationContext {

    private final Map<ActionMetadata, ActionContext> contexts = new HashMap<>();

    /** The row metadata. */
    private RowMetadata transformedRowMetadata;

    /** The context itself. */
    private Map<String, Object> context;

    /**
     * Default empty constructor.
     */
    public TransformationContext() {
        context = new HashMap<>();
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

    /**
     * @param transformedRowMetadata the row metadata to build this context from.
     */
    public void setTransformedRowMetadata(RowMetadata transformedRowMetadata) {
        this.transformedRowMetadata = transformedRowMetadata;
    }

    /**
     * Put the given value at the given key in the context.
     *
     * @param key where to put the value.
     * @param value the value to store.
     */
    public void put(String key, Object value) {
        context.put(key, value);
    }

    /**
     * Return the wanted value.
     *
     * @param key where to look for the value in the context.
     * @return the wanted value or null if not found.
     */
    public Object get(String key) {
        return context.get(key);
    }

    /**
     * Returns a transformation context specific to the current action. Use this to create columns (see
     * {@link ActionContext#column(String, Supplier, Consumer)} for more details).
     * 
     * @param actionMetadata An instance of action used as key for finding context
     * @return An {@link ActionContext context} ready to be used.
     */
    public ActionContext in(ActionMetadata actionMetadata) {
        if (contexts.containsKey(actionMetadata)) {
            return contexts.get(actionMetadata);
        } else {
            ActionContext actionContext = new ActionContext(this);
            contexts.put(actionMetadata, actionContext);
            return actionContext;
        }
    }
}
