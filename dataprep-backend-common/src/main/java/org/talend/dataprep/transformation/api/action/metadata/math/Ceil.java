// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprep.transformation.api.action.metadata.math;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.DataSetRowAction;
import org.talend.dataprep.transformation.api.action.metadata.ActionCategory;
import org.talend.dataprep.transformation.api.action.metadata.ActionMetadata;
import org.talend.dataprep.transformation.api.action.metadata.SingleColumnAction;

/**
 * This will compute the Returns the smallest (closest to negative infinity) value that is greater than or equal to the
 * value and is equal to a mathematical integer.
 * 
 * @see Math#ceil(double)
 */
@Component(Ceil.ACTION_BEAN_PREFIX + Ceil.CELL_ACTION_NAME)
public class Ceil extends SingleColumnAction {

    /** The action name. */
    public static final String CELL_ACTION_NAME = "ceil"; //$NON-NLS-1$

    /**
     * @see ActionMetadata#getName()
     */
    @Override
    public String getName() {
        return CELL_ACTION_NAME;
    }

    /**
     * @see ActionMetadata#getCategory()
     */
    @Override
    public String getCategory() {
        return ActionCategory.MATH.getDisplayName();
    }

    /**
     * @see ActionMetadata#create(Map)
     */
    @Override
    public DataSetRowAction create(Map<String, String> parameters) {
        return (row, context) -> {
            String columnName = parameters.get(COLUMN_ID);
            String value = row.get(columnName);
            if (value != null) {
                try {
                    int result = (int) Math.ceil(Double.valueOf(value));
                    row.set(columnName, String.valueOf(result));
                } catch (NumberFormatException nfe2) {
                    // Nan: nothing to do, but fail silently (no change in value)
                }
            }
        };
    }

    /**
     * @see ActionMetadata#accept(ColumnMetadata)
     */
    @Override
    public boolean accept(ColumnMetadata column) {
        Type columnType = Type.get(column.getType());
        // in order to 'clean' integer typed columns, this function needs to be allowed on any numeric types
        return Type.NUMERIC.isAssignableFrom(columnType);
    }
}
