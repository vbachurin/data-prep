package org.talend.dataprep.transformation.api.action.metadata;

import java.util.Collections;
import java.util.Set;

import javax.annotation.Nonnull;

import org.springframework.stereotype.Component;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.parameters.Item;
import org.talend.dataprep.transformation.api.action.parameters.Parameter;

@Component(value = FillWithDefaultIfEmptyInteger.ACTION_BEAN_PREFIX + FillWithDefaultIfEmptyInteger.FILL_EMPTY_ACTION_NAME)
public class FillWithDefaultIfEmptyInteger extends AbstractDefaultIfEmpty {

    public static final String FILL_EMPTY_ACTION_NAME = "fillemptywithdefaultinteger"; //$NON-NLS-1$

    private FillWithDefaultIfEmptyInteger() {
    }

    @Override
    public String getName() {
        return FILL_EMPTY_ACTION_NAME;
    }

    @Override
    @Nonnull
    public Parameter[] getParameters() {
        //@formatter:off
        return new Parameter[] {
                COLUMN_ID_PARAMETER,
                COLUMN_NAME_PARAMETER,
                new Parameter(DEFAULT_VALUE_PARAMETER, Type.INTEGER.getName(), "0") }; //$NON-NLS-1$
        //@formatter:on
    }

    @Override
    @Nonnull
    public Item[] getItems() {
        return new Item[0];
    }

    @Override
    public Set<Type> getCompatibleColumnTypes() {
        return Collections.singleton(Type.INTEGER);
    }

}
