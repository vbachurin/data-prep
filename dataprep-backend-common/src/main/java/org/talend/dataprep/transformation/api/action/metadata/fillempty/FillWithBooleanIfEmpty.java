package org.talend.dataprep.transformation.api.action.metadata.fillempty;

import javax.annotation.Nonnull;

import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;
import org.talend.dataprep.transformation.api.action.parameters.Item;
import org.talend.dataprep.transformation.api.action.parameters.Item.Value;

@Component(FillWithBooleanIfEmpty.ACTION_BEAN_PREFIX + FillWithBooleanIfEmpty.FILL_EMPTY_ACTION_NAME)
public class FillWithBooleanIfEmpty extends AbstractFillIfEmpty {

    public static final String FILL_EMPTY_ACTION_NAME = "fillemptywithdefaultboolean"; //$NON-NLS-1$

    @Override
    public String getName() {
        return FILL_EMPTY_ACTION_NAME;
    }

    @Override
    @Nonnull
    public Item[] getItems() {
        final Value[] values = new Value[] { new Value("True", true), new Value("False") }; //$NON-NLS-1$//$NON-NLS-2$
        return new Item[] { new Item(DEFAULT_VALUE_PARAMETER, "categ", values) }; //$NON-NLS-1$
    }

    /**
     * @see ActionMetadata#acceptColumn(ColumnMetadata)
     */
    @Override
    public boolean acceptColumn(ColumnMetadata column) {
        return Type.BOOLEAN.equals(Type.get(column.getType()));
    }

}
