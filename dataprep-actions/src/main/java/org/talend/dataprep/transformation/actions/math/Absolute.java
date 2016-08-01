// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// https://github.com/Talend/data-prep/blob/master/LICENSE
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprep.transformation.actions.math;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.talend.daikon.number.BigDecimalParser;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.actions.category.ActionCategory;
import org.talend.dataprep.transformation.actions.common.AbstractActionMetadata;
import org.talend.dataprep.transformation.actions.common.ColumnAction;
import org.talend.dataprep.transformation.actions.common.DataprepAction;
import org.talend.dataprep.transformation.api.action.context.ActionContext;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.Set;

/**
 * This will compute the absolute value for numerical columns.
 */
@DataprepAction(AbstractActionMetadata.ACTION_BEAN_PREFIX + Absolute.ABSOLUTE_ACTION_NAME)
@Scope(value = "prototype")
public class Absolute extends AbstractActionMetadata implements ColumnAction {

    public static final String ABSOLUTE_ACTION_NAME = "absolute"; //$NON-NLS-1$

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public String getName() {
        return ABSOLUTE_ACTION_NAME;
    }

    @Override
    public String getCategory() {
        return ActionCategory.MATH.getDisplayName();
    }

    @Override
    public boolean acceptColumn(ColumnMetadata column) {
        return Type.FLOAT.equals(Type.get(column.getType())) //
                || Type.DOUBLE.equals(Type.get(column.getType())) //
                || Type.INTEGER.equals(Type.get(column.getType()));
    }

    @Override
    public void applyOnColumn(DataSetRow row, ActionContext context) {
        final String columnId = context.getColumnId();
        final String value = row.get(columnId);
        if (value == null) {
            return;
        }
        String absValueStr = executeAbs(value);
        if (absValueStr != null) {
            row.set(columnId, absValueStr);
        }
    }

    private String executeAbs(final String value) {
        try {
            BigDecimal bd = BigDecimalParser.toBigDecimal(value);
            return bd.abs().toPlainString();
        } catch (NumberFormatException nfe2) {
            return null;
        }
    }

    @Override
    public Set<Behavior> getBehavior() {
        return EnumSet.of(Behavior.VALUES_COLUMN);
    }

}
