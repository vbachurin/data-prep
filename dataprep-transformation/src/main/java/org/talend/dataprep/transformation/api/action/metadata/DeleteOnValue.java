package org.talend.dataprep.transformation.api.action.metadata;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.talend.dataprep.api.type.Types;

public class DeleteOnValue extends AbstractDelete {

    public static final String         DELETE_ON_VALUE_ACTION_NAME = "delete_on_value";                                              //$NON-NLS-1$

    public static final String         DELETE_ON_VALUE_ACTION_DESC = "Delete rows that have a specific value in cell in this column"; //$NON-NLS-1$

    public static final String         VALUE_PARAMETER             = "value";                                                        //$NON-NLS-1$

    public static final ActionMetadata INSTANCE                    = new DeleteOnValue();

    private DeleteOnValue() {
    }

    @Override
    public String getName() {
        return DELETE_ON_VALUE_ACTION_NAME;
    }

    @Override
    public String getDescription() {
        return DELETE_ON_VALUE_ACTION_DESC;
    }

    @Override
    public Parameter[] getParameters() {
        return new Parameter[] { new Parameter(COLUMN_NAME_PARAMETER, Types.STRING.getName(), StringUtils.EMPTY),
                new Parameter(VALUE_PARAMETER, Types.STRING.getName(), StringUtils.EMPTY) };
    }

    @Override
    public boolean toDelete(Map<String, String> parsedParameters, String value) {
        return (value != null && value.trim().equals(parsedParameters.get(VALUE_PARAMETER)));
    }

}
