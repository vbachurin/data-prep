package org.talend.dataprep.transformation.api.action.metadata;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.preparation.Action;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.metadata.category.ActionCategory;
import org.talend.dataprep.transformation.api.action.metadata.common.SingleColumnAction;
import org.talend.dataprep.transformation.api.action.parameters.Parameter;

import java.util.Map;

import static org.talend.dataprep.api.preparation.Action.Builder.builder;

@Component(ReplaceOnValue.ACTION_BEAN_PREFIX + ReplaceOnValue.REPLACE_ON_VALUE_ACTION_NAME)
public class ReplaceOnValue extends SingleColumnAction {

    /** The action name. */
    public static final String REPLACE_ON_VALUE_ACTION_NAME = "replace_on_value"; //$NON-NLS-1$

    /** Value to match */
    public static final String CELL_VALUE_PARAMETER = "cell_value"; //$NON-NLS-1$

    /** Replace Value */
    public static final String REPLACE_VALUE_PARAMETER = "replace_value"; //$NON-NLS-1$

    /**
     * @see ActionMetadata#getName()
     */
    @Override
    public String getName() {
        return REPLACE_ON_VALUE_ACTION_NAME;
    }

    /**
     * @see ActionMetadata#getCategory()
     */
    @Override
    public String getCategory() {
        return ActionCategory.QUICKFIX.getDisplayName();
    }

    /**
     * @see ActionMetadata#getCategory()
     */
    @Override
    public Parameter[] getParameters() {
        return new Parameter[] {COLUMN_ID_PARAMETER,
                COLUMN_NAME_PARAMETER,
                new Parameter(CELL_VALUE_PARAMETER, Type.STRING.getName(), StringUtils.EMPTY),
                new Parameter(REPLACE_VALUE_PARAMETER, Type.STRING.getName(), StringUtils.EMPTY)};
    }

    /**
     * @see ActionMetadata#create(Map)
     */
    @Override
    public Action create(Map<String, String> parameters) {
        return builder().withRow((row, context) -> {
            final String columnName = parameters.get(COLUMN_ID);
            final String value = row.get(columnName);
            final String toMatch = parameters.get(CELL_VALUE_PARAMETER);

            if (toMatch.equals(value)) {
                final String toReplace = parameters.get(REPLACE_VALUE_PARAMETER);
                row.set(columnName, toReplace);
            }

            return row;
        }).build();
    }

    /**
     *
     * @see ActionMetadata#accept(ColumnMetadata)
     */
    @Override
    public boolean accept(ColumnMetadata column) {
        return Type.STRING.equals(Type.get(column.getType()));
    }
}
