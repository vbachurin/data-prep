package org.talend.dataprep.transformation.api.action.metadata;

import java.util.Collections;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.type.Type;

@Component(FillWithDefaultIfEmpty.ACTION_BEAN_PREFIX + FillWithDefaultIfEmpty.FILL_EMPTY_ACTION_NAME)
public class FillWithDefaultIfEmpty extends AbstractDefaultIfEmpty {

    public static final String FILL_EMPTY_ACTION_NAME = "fillemptywithdefault"; //$NON-NLS-1$

    public static final ActionMetadata INSTANCE = new FillWithDefaultIfEmpty();

    private FillWithDefaultIfEmpty() {
    }

    @Override
    public String getName() {
        return FILL_EMPTY_ACTION_NAME;
    }

    @Override
    public Item[] getItems() {
        return new Item[0];
    }

    @Override
    public Parameter[] getParameters() {
        return new Parameter[] { new Parameter(COLUMN_NAME_PARAMETER, Type.STRING.getName(), StringUtils.EMPTY),
                new Parameter(DEFAULT_VALUE_PARAMETER, Type.STRING.getName(), StringUtils.EMPTY) };
    }

    @Override
    public Set<Type> getCompatibleColumnTypes() {
        return Collections.singleton(Type.STRING);
    }

}
