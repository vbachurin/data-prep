package org.talend.dataprep.transformation.api.action.metadata.text;

import java.util.Map;

import javax.annotation.Nonnull;

import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;
import org.talend.dataprep.transformation.api.action.metadata.category.ActionCategory;
import org.talend.dataprep.transformation.api.action.metadata.common.AbstractActionMetadata;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;
import org.talend.dataprep.transformation.api.action.metadata.common.IColumnAction;
import org.talend.dataprep.transformation.api.action.parameters.Item;

/**
 * Trim leading and trailing spaces.
 */
@Component(Trim.ACTION_BEAN_PREFIX + Trim.TRIM_ACTION_NAME)
public class Trim extends AbstractActionMetadata implements IColumnAction {

    /**
     * The action name.
     */
    public static final String TRIM_ACTION_NAME = "trim"; //$NON-NLS-1$

    /**
     * @see ActionMetadata#getName()
     */
    @Override
    public String getName() {
        return TRIM_ACTION_NAME;
    }

    /**
     * @see ActionMetadata#getCategory()
     */
    @Override
    public String getCategory() {
        return ActionCategory.QUICKFIX.getDisplayName();
    }

    /**
     * @see ActionMetadata#getItems()
     */
    @Override
    @Nonnull
    public Item[] getItems() {
        return new Item[0];
    }

    /**
     * @see ActionMetadata#acceptColumn(ColumnMetadata)
     */
    @Override
    public boolean acceptColumn(ColumnMetadata column) {
        return Type.STRING.equals(Type.get(column.getType()));
    }

    @Override
    protected void beforeApply(Map<String, String> parameters) {
    }

    @Override
    public void applyOnColumn(DataSetRow row, TransformationContext context, Map<String, String> parameters, String columnId) {
        final String value = row.get(columnId);
        if (value != null) {
            row.set(columnId, value.trim());
        }
    }
}
