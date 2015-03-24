package org.talend.dataprep.transformation.api.action.metadata;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.talend.dataprep.api.type.Types;
import org.talend.dataprep.transformation.api.action.metadata.Item.Value;

@Configuration
public class FillWithDefaultIfEmptyBoolean extends AbstractDefaultIfEmpty {

    public static final String FILL_EMPTY_ACTION_NAME = "fillemptywithdefaultboolean"; //$NON-NLS-1$

    public static final ActionMetadata INSTANCE = new FillWithDefaultIfEmptyBoolean();

    @Bean(name = ACTION_BEAN_PREFIX + FILL_EMPTY_ACTION_NAME)
    public ActionMetadata createInstance() {
        return new FillWithDefaultIfEmptyBoolean();
    }

    // Please do not instanciate this class, it is spring Bean automatically instanciated.
    public FillWithDefaultIfEmptyBoolean() {
    }

    @Override
    public String getName() {
        return FILL_EMPTY_ACTION_NAME;
    }

    @Override
    public Parameter[] getParameters() {
        return new Parameter[] { new Parameter(COLUMN_NAME_PARAMETER, Types.STRING.getName(), StringUtils.EMPTY) };
    }

    @Override
    public Item[] getItems() {
        Value[] values = new Value[] { new Value("True", true), new Value("False") }; //$NON-NLS-1$//$NON-NLS-2$
        return new Item[] { new Item(DEFAULT_VALUE_PARAMETER, "categ", values) }; //$NON-NLS-1$
    }

}
