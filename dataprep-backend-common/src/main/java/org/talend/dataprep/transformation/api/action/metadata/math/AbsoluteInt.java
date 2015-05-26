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

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.metadata.SingleColumnAction;
import org.talend.dataprep.transformation.api.action.parameters.Item;

/**
 * This will compute the absolute value for numerical columns
 *
 */
@Component(AbsoluteInt.ACTION_BEAN_PREFIX + AbsoluteInt.ABSOLUTE_INT_ACTION_NAME)
public class AbsoluteInt extends SingleColumnAction {

    public static final String ABSOLUTE_INT_ACTION_NAME = "absolute_int"; //$NON-NLS-1$

    private AbsoluteInt() {
    }

    @Override
    public String getName() {
        return ABSOLUTE_INT_ACTION_NAME;
    }

    @Override
    public String getCategory() {
        return "math"; //$NON-NLS-1$
    }

    @Override
    @Nonnull
    public Item[] getItems() {
        return new Item[0];
    }

    @Override
    public Consumer<DataSetRow> create(Map<String, String> parameters) {
        return row -> {
            String columnName = parameters.get(COLUMN_NAME_PARAMETER_NAME);
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

    @Override
    public Set<Type> getCompatibleColumnTypes() {
        return Collections.singleton(Type.INTEGER);
    }

}
