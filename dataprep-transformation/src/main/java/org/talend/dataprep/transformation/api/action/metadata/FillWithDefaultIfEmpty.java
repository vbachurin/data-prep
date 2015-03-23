package org.talend.dataprep.transformation.api.action.metadata;

import org.apache.commons.lang.StringUtils;
import org.talend.dataprep.api.type.Types;

public class FillWithDefaultIfEmpty extends AbstractDefaultIfEmpty {

    public static final String         FILL_EMPTY_ACTION_NAME  = "fillemptywithdefault";                            //$NON-NLS-1$

    public static final ActionMetadata INSTANCE                = new FillWithDefaultIfEmpty();

    protected FillWithDefaultIfEmpty() {
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
