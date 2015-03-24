package org.talend.dataprep.transformation.api.action.metadata;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.talend.dataprep.api.type.Types;

@Configuration
public class FillWithDefaultIfEmpty extends AbstractDefaultIfEmpty {

    public static final String FILL_EMPTY_ACTION_NAME = "fillemptywithdefault"; //$NON-NLS-1$

    public static final ActionMetadata INSTANCE = new FillWithDefaultIfEmpty();

    @Bean(name = ACTION_BEAN_PREFIX + FILL_EMPTY_ACTION_NAME)
    public ActionMetadata createInstance() {
        return new FillWithDefaultIfEmpty();
    }

    // Please do not instanciate this class, it is spring Bean automatically instanciated.
    public FillWithDefaultIfEmpty() {
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
        return new Parameter[] { new Parameter(COLUMN_NAME_PARAMETER, Types.STRING.getName(), StringUtils.EMPTY),
                new Parameter(DEFAULT_VALUE_PARAMETER, Types.STRING.getName(), StringUtils.EMPTY) };
    }

}
