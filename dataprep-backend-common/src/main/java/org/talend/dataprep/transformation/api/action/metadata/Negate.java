package org.talend.dataprep.transformation.api.action.metadata;

import java.util.Map;
import java.util.function.BiConsumer;

import org.apache.commons.lang.WordUtils;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;

/**
 * Negate a boolean.
 * 
 * @see Negate
 */
@Component(Negate.ACTION_BEAN_PREFIX + Negate.NEGATE_ACTION_NAME)
public class Negate extends SingleColumnAction {

    /** Action name. */
    public static final String NEGATE_ACTION_NAME = "negate"; //$NON-NLS-1$

    /**
     * @see ActionMetadata#getName()
     */
    @Override
    public String getName() {
        return NEGATE_ACTION_NAME;
    }

    /**
     * @see ActionMetadata#getCategory()
     */
    @Override
    public String getCategory() {
        return "boolean"; //$NON-NLS-1$
    }

    /**
     * @see ActionMetadata#create(Map)
     */
    @Override
    public BiConsumer<DataSetRow, TransformationContext> create(Map<String, String> parameters) {
        return (row, context) -> {
            String columnName = parameters.get(COLUMN_ID);
            String value = row.get(columnName);

            if (value != null && ("true".equalsIgnoreCase(value.trim()) || "false".equalsIgnoreCase(value.trim()))) { //$NON-NLS-1$ //$NON-NLS-2$
                Boolean boolValue = Boolean.valueOf(value);
                row.set(columnName, WordUtils.capitalizeFully("" + !boolValue)); //$NON-NLS-1$
            }
        };
    }

    /**
     * @see ActionMetadata#accept(ColumnMetadata)
     */
    @Override
    public boolean accept(ColumnMetadata column) {
        return Type.BOOLEAN.equals(Type.get(column.getType()));
    }

}
