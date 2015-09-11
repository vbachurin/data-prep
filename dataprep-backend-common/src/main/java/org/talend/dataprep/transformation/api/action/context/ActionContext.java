package org.talend.dataprep.transformation.api.action.context;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.talend.dataprep.api.dataset.ColumnMetadata;

public class ActionContext {

    private final Map<String, ColumnMetadata> columns = new HashMap<>();

    private final TransformationContext parent;

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
     *
     * @return A {@link ColumnMetadata column} with name <code>name</code>.
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
            postCreate.accept(newColumn);
            columns.put(name, newColumn);
            return newColumn.getId();
        }
    }

}
