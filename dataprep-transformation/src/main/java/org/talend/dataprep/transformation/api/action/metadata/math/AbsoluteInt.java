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

import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.metadata.Item;
import org.talend.dataprep.transformation.api.action.metadata.Parameter;
import org.talend.dataprep.transformation.api.action.metadata.SingleColumnAction;

/**
 * This will compute the absolute value for numerical columns
 *
 */
@Component(AbsoluteInt.ACTION_BEAN_PREFIX + AbsoluteInt.ABSOLUTE_INT_ACTION_NAME)
public class AbsoluteInt extends SingleColumnAction {

    public static final String ABSOLUTE_INT_ACTION_NAME = "absolute_int"; //$NON-NLS-1$

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprep.transformation.api.action.metadata.ActionMetadata#getName()
     */
    @Override
    public String getName() {
        return ABSOLUTE_INT_ACTION_NAME;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprep.transformation.api.action.metadata.ActionMetadata#getCategory()
     */
    @Override
    public String getCategory() {
        return "math";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprep.transformation.api.action.metadata.ActionMetadata#getItems()
     */
    @Override
    public Item[] getItems() {
        return new Item[0];
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprep.transformation.api.action.metadata.ActionMetadata#getParameters()
     */
    @Override
    public Parameter[] getParameters() {
        return new Parameter[] { COLUMN_NAME_PARAMETER };
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprep.transformation.api.action.metadata.ActionMetadata#create(java.util.Map)
     */
    @Override
    public Consumer<DataSetRow> create(Map<String, String> parsedParameters) {
        return row -> {
            String columnName = parsedParameters.get(COLUMN_NAME_PARAMETER_NAME);
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
                        // the value is not a long nor a float so ignors it
                        // and let absValue to be null.
                    }
                }
                if (absValueStr != null) {
                    row.set(columnName, absValueStr);
                }// else not a int or a float to do nothing.
            }// else no value set for this column so do nothing

        };
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprep.transformation.api.action.metadata.ActionMetadata#getCompatibleColumnTypes()
     */
    @Override
    public Set<Type> getCompatibleColumnTypes() {
        return Collections.singleton(Type.INTEGER);
    }

}
