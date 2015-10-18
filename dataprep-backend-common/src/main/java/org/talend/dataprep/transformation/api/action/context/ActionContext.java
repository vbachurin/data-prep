package org.talend.dataprep.transformation.api.action.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.talend.dataprep.api.dataset.ColumnMetadata;

public class ActionContext {

    private final TransformationContext parent;

    private Map<String, ColumnMetadata> columns = new HashMap<>();

    public ActionContext(TransformationContext parent) {
        this.parent = parent;
    }

    public TransformationContext getParent() {
        return parent;
    }

    /**
     * <p>
     * Returns the column with logical name <code>name</code> or creates a new one with provided {@link Supplier
     * supplier}.
     * </p>
     * <p>
     * <b>Note</b>It is up to the caller to insert column in a {@link org.talend.dataprep.api.dataset.RowMetadata row}.
     * </p>
     * 
     * @param name A column name as string. All values are accepted, no collision with other action can occur.
     * @param supplier A {@link Supplier supplier} that provides a new {@link ColumnMetadata} in case no column with
     * <code>name</code> was previously created. Supplier is <b>not</b> allowed to return <code>null</code>.
     * @param postCreate A {@link Consumer consumer} to perform changes on column created by <code>supplier</code>. A
     * default post create would be to insert column in a {@link org.talend.dataprep.api.dataset.RowMetadata row}.
     * 
     * @return A {@link ColumnMetadata column} id with name <code>name</code>.
     * @throws IllegalArgumentException In case the <code>supplier</code> returned a <code>null</code> instance.
     */
    public String column(String name, Supplier<ColumnMetadata> supplier, Consumer<ColumnMetadata> postCreate) {
        if (columns.containsKey(name)) {
            return columns.get(name).getId();
        } else {
            final ColumnMetadata newColumn = supplier.get();
            if (newColumn == null) {
                throw new IllegalArgumentException("Cannot use a null column for '" + name + "'");
            }
            newColumn.setId(null); // Ensure no id is set before calling postCreate (postCreate will set the correct id)
            if (postCreate == null) {
                throw new IllegalArgumentException("Post create parameter can not be null.");
            }
            postCreate.accept(newColumn);
            columns.put(name, newColumn);
            return newColumn.getId();
        }
    }

    /**
     * @return A new {@link ActionContext} instance that can not be modified: an immutable ActionContext will <b>not</b>
     * let action add a new column, but allows to get previously created ones.
     */
    public ActionContext asImmutable() {
        final ActionContext actionContext = new ActionContext(parent);
        actionContext.columns = Collections.unmodifiableMap(this.columns);
        return actionContext;
    }
}
