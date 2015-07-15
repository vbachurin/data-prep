package org.talend.dataprep.transformation.api.action.metadata;

import static org.talend.dataprep.api.preparation.Action.Builder.builder;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.preparation.Action;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.parameters.Parameter;

@Component(Substring.ACTION_BEAN_PREFIX + Substring.SUBSTRING_ACTION_NAME)
public class Substring extends SingleColumnAction {

    /** The action name. */
    public static final String SUBSTRING_ACTION_NAME = "substring"; //$NON-NLS-1$

    public static final String FROM_PARAMETER = "from"; //$NON-NLS-1$

    public static final String TO_PARAMETER = "to"; //$NON-NLS-1$

    /**
     * @see ActionMetadata#getName()
     */
    @Override
    public String getName() {
        return SUBSTRING_ACTION_NAME;
    }

    /**
     * @see ActionMetadata#getCategory()
     */
    @Override
    public String getCategory() {
        return ActionCategory.QUICKFIX.getDisplayName();
    }

    /**
     * @see ActionMetadata#getParameters()
     */
    @Override
    public Parameter[] getParameters() {
        return new Parameter[] { COLUMN_ID_PARAMETER, COLUMN_NAME_PARAMETER,
                new Parameter(FROM_PARAMETER, Type.NUMERIC.getName(), StringUtils.EMPTY),
                new Parameter(TO_PARAMETER, Type.NUMERIC.getName(), StringUtils.EMPTY) };
    }

    /**
     * @see ActionMetadata#create(Map)
     */
    @Override
    public Action create(Map<String, String> parameters) {
        String columnName = parameters.get(COLUMN_ID);
        int fromIndex = Integer.parseInt(parameters.get(FROM_PARAMETER));
        int toIndex = Integer.parseInt(parameters.get(TO_PARAMETER));

        return builder().withRow((row, context) -> {
            String value = row.get(columnName);
            if (value != null) {
                String newValue = value.substring(fromIndex, toIndex);
                row.set(columnName, newValue);
            }
        }).build();
    }

    /**
     * @see ActionMetadata#accept(ColumnMetadata)
     */
    @Override
    public boolean accept(ColumnMetadata column) {
        return Type.STRING.equals(Type.get(column.getType()));
    }
}
