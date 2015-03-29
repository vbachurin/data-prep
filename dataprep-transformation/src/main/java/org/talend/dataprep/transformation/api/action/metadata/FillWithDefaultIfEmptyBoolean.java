package org.talend.dataprep.transformation.api.action.metadata;

import java.util.Collections;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.metadata.Item.Value;

@Component(FillWithDefaultIfEmptyBoolean.ACTION_BEAN_PREFIX + FillWithDefaultIfEmptyBoolean.FILL_EMPTY_ACTION_NAME)
public class FillWithDefaultIfEmptyBoolean extends AbstractDefaultIfEmpty {

    public static final String FILL_EMPTY_ACTION_NAME = "fillemptywithdefaultboolean"; //$NON-NLS-1$

    public static final ActionMetadata INSTANCE = new FillWithDefaultIfEmptyBoolean();

    private FillWithDefaultIfEmptyBoolean() {
    }

    @Override
    public String getName() {
        return FILL_EMPTY_ACTION_NAME;
    }

    @Override
    public Parameter[] getParameters() {
        return new Parameter[] { new Parameter(COLUMN_NAME_PARAMETER, Type.STRING.getName(), StringUtils.EMPTY) };
    }

    @Override
    public Item[] getItems() {
        Value[] values = new Value[] { new Value("True", true), new Value("False") }; //$NON-NLS-1$//$NON-NLS-2$
        return new Item[] { new Item(DEFAULT_VALUE_PARAMETER, "categ", values) }; //$NON-NLS-1$
    }

    @Override
    public Set<Type> getCompatibleColumnTypes() {
        return Collections.singleton(Type.BOOLEAN);
    }

}
