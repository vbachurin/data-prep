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

import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;
import org.talend.dataprep.transformation.api.action.metadata.category.ActionCategory;
import org.talend.dataprep.transformation.api.action.metadata.common.AbstractActionMetadata;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;
import org.talend.dataprep.transformation.api.action.metadata.common.ColumnAction;

/**
 * This will compute the largest (closest to positive infinity) value that is less than or equal to the cell value and
 * is equal to a mathematical integer.
 *
 * @see Math#floor(double)
 */
public abstract class AbstractRound extends AbstractActionMetadata implements ColumnAction {

    /**
     * @see ActionMetadata#getCategory()
     */
    @Override
    public String getCategory() {
        return ActionCategory.MATH.getDisplayName();
    }

    @Override
    public void applyOnColumn(final DataSetRow row, final TransformationContext context, final Map<String, String> parameters, final String columnId) {
        final String value = row.get(columnId);
        if (value == null) {
            return;
        }

        try {
            int result = compute(Double.valueOf(value));
            row.set(columnId, String.valueOf(result));
        } catch (NumberFormatException nfe2) {
            // Nan: nothing to do, but fail silently (no change in value)
        }
    }

    protected abstract int compute(double from);

    /**
     * @see ActionMetadata#acceptColumn(ColumnMetadata)
     */
    @Override
    public boolean acceptColumn(ColumnMetadata column) {
        Type columnType = Type.get(column.getType());
        // in order to 'clean' integer typed columns, this function needs to be allowed on any numeric types
        return Type.NUMERIC.isAssignableFrom(columnType);
    }
}
