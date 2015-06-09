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
import java.util.function.BiConsumer;

import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;
import org.talend.dataprep.transformation.api.action.metadata.ActionMetadata;
import org.talend.dataprep.transformation.api.action.metadata.SingleColumnAction;

/**
 * This will compute the absolute value for numerical columns
 *
 */
@Component(AbsoluteInt.ACTION_BEAN_PREFIX + AbsoluteInt.ABSOLUTE_INT_ACTION_NAME)
public class AbsoluteInt extends SingleColumnAction {

    public static final String ABSOLUTE_INT_ACTION_NAME = "absolute_int"; //$NON-NLS-1$

    @Override
    public String getName() {
        return ABSOLUTE_INT_ACTION_NAME;
    }

    @Override
    public String getCategory() {
        return "math"; //$NON-NLS-1$
    }

    @Override
    public BiConsumer<DataSetRow, TransformationContext> create(Map<String, String> parameters) {
        return (row, context) -> {
            String columnName = parameters.get(COLUMN_ID);
            String value = row.get(columnName);
            String absValueStr = null;
            if (value != null) {
                // try long first
                try {
                    long longValue = Long.parseLong(value);
                    absValueStr = Long.toString(Math.abs(longValue));
                } catch (NumberFormatException nfe1) {
                    // try float
                    try {
                        double doubleValue = Double.parseDouble(value);
                        double absValue = Math.abs(doubleValue);
                        if (absValue == (long) absValue) {// this will prevent having .0 for longs.
                            absValueStr = String.format("%d", (long) absValue); //$NON-NLS-1$
                        } else {
                            absValueStr = String.format("%s", absValue); //$NON-NLS-1$
                        }
                    } catch (NumberFormatException nfe2) {
                        // the value is not a long nor a float so ignores it
                        // and let absValue to be null.
                    }
                }
                if (absValueStr != null) {
                    row.set(columnName, absValueStr);
                }// else not a int or a float to do nothing.
            }// else no value set for this column so do nothing

        };
    }

    /**
     * @see ActionMetadata#accept(ColumnMetadata)
     */
    @Override
    public boolean accept(ColumnMetadata column) {
        return Type.INTEGER.equals(Type.get(column.getType()));
    }

}
