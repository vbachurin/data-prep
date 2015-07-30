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

import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;
import org.talend.dataprep.transformation.api.action.metadata.category.ActionCategory;
import org.talend.dataprep.transformation.api.action.metadata.common.IColumnAction;

import java.util.Map;

/**
 * This will compute the absolute value for numerical columns.
 * This action is faster than the AbsoluteInt one on columns with more float values
 */
@Component(AbsoluteFloat.ACTION_BEAN_PREFIX + AbsoluteFloat.ABSOLUTE_FLOAT_ACTION_NAME)
public class AbsoluteFloat extends AbstractAbsolute implements IColumnAction {

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
    public boolean acceptColumn(ColumnMetadata column) {
        return Type.FLOAT.equals(Type.get(column.getType())) || Type.DOUBLE.equals(Type.get(column.getType()));
    }

    @Override
    protected void beforeApply(Map<String, String> parameters) {
    }

    @Override
    public void applyOnColumn(DataSetRow row, TransformationContext context, Map<String, String> parameters, String columnId) {
        final String value = row.get(columnId);
        if (value == null) {
            return;
        }

        String absValueStr = executeOnFloat(value);
        if (absValueStr == null) {
            absValueStr = executeOnLong(value);
        }
        if (absValueStr != null) {
            row.set(columnId, absValueStr);
        }
    }
}
