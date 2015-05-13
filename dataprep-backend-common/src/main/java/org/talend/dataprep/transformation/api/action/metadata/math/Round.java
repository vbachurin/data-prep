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
import org.talend.dataprep.transformation.api.action.metadata.Item;
import org.talend.dataprep.transformation.api.action.metadata.SingleColumnAction;

/**
 * This will compute the absolute value for numerical columns
 *
 */
@Component(Round.ACTION_BEAN_PREFIX + Round.ROUND_ACTION_NAME)
public class Round extends SingleColumnAction {

    public static final String ROUND_ACTION_NAME = "round"; //$NON-NLS-1$

    private Round() {
    }

    @Override
    public String getName() {
        return ROUND_ACTION_NAME;
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
                try {
                    double doubleValue = Double.parseDouble(value);
                    long roundedValue = Math.round(doubleValue);
                    absValueStr = String.format("%s", roundedValue); //$NON-NLS-1$
                } catch (NumberFormatException nfe2) {
                    // Nan: nothing to do, but fail silently (no change in value)
                }
                if (absValueStr != null) {
                    row.set(columnName, absValueStr);
                }
            }

        };
    }

    @Override
    public Set<Type> getCompatibleColumnTypes() {
        return Collections.singleton(Type.NUMERIC);
    }

}
