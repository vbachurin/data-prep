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

import static org.talend.dataprep.api.preparation.Action.Builder.builder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;

import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.preparation.Action;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.metadata.category.ActionCategory;
import org.talend.dataprep.transformation.api.action.metadata.ActionMetadata;
import org.talend.dataprep.transformation.api.action.metadata.common.SingleColumnAction;

/**
 * This will compute the absolute value for numerical columns.
 * This action is faster than the AbsoluteInt one on columns with more float values
 */
@Component(AbsoluteFloat.ACTION_BEAN_PREFIX + AbsoluteFloat.ABSOLUTE_FLOAT_ACTION_NAME)
public class AbsoluteFloat extends AbstractAbsolute {

    public static final String ABSOLUTE_FLOAT_ACTION_NAME = "absolute_float"; //$NON-NLS-1$

    @Override
    public String getName() {
        return ABSOLUTE_FLOAT_ACTION_NAME;
    }

    @Override
    public String getCategory() {
        return ActionCategory.MATH.getDisplayName();
    }

    @Override
    public Action create(Map<String, String> parameters) {
        return builder().withRow((row, context) -> {
            final String columnName = parameters.get(COLUMN_ID);
            final String value = row.get(columnName);
            String absValueStr;

            if (value != null) {
                absValueStr = executeOnFloat(value);
                if(absValueStr == null) {
                    absValueStr = executeOnLong(value);
                }
                if(absValueStr != null) {
                    row.set(columnName, absValueStr);
                }
            }
        }).build();
    }

    /**
     * @see ActionMetadata#accept(ColumnMetadata)
     */
    @Override
    public boolean accept(ColumnMetadata column) {
        return Type.FLOAT.equals(Type.get(column.getType())) || Type.DOUBLE.equals(Type.get(column.getType()));
    }
}
