package org.talend.dataprep.transformation.api.action.metadata;

import java.util.Collections;
import java.util.Set;

import javax.annotation.Nonnull;

import org.springframework.stereotype.Component;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.parameters.Item;
import org.talend.dataprep.transformation.api.action.parameters.Item.Value;
import org.talend.dataprep.transformation.api.action.parameters.Parameter;

@Component(FillWithDefaultIfEmptyBoolean.ACTION_BEAN_PREFIX + FillWithDefaultIfEmptyBoolean.FILL_EMPTY_ACTION_NAME)
public class FillWithDefaultIfEmptyBoolean extends AbstractDefaultIfEmpty {

    public static final String FILL_EMPTY_ACTION_NAME = "fillemptywithdefaultboolean"; //$NON-NLS-1$

    private FillWithDefaultIfEmptyBoolean() {
    }

    @Override
    public String getName() {
        return FILL_EMPTY_ACTION_NAME;
    }

    @Override
    @Nonnull
    public Parameter[] getParameters() {
        return new Parameter[] { COLUMN_ID_PARAMETER };
    }

    @Override
    @Nonnull
    public Item[] getItems() {
        Value[] values = new Value[] { new Value("True", true), new Value("False") }; //$NON-NLS-1$//$NON-NLS-2$
        return new Item[] { new Item(DEFAULT_VALUE_PARAMETER, "categ", values) }; //$NON-NLS-1$
    }

    @Override
    public Set<Type> getCompatibleColumnTypes() {
        return Collections.singleton(Type.BOOLEAN);
    }

}
