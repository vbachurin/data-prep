//  ============================================================================
//
//  Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
//  This source code is available under agreement available at
//  https://github.com/Talend/data-prep/blob/master/LICENSE
//
//  You should have received a copy of the agreement
//  along with this program; if not, write to Talend SA
//  9 rue Pages 92150 Suresnes, France
//
//  ============================================================================

package org.talend.dataprep.transformation.api.action.metadata.delete;

import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.transformation.api.action.context.ActionContext;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadataAdapter;
import org.talend.dataprep.transformation.api.action.metadata.common.ColumnAction;

import java.util.EnumSet;
import java.util.Set;

import static org.talend.dataprep.transformation.api.action.metadata.category.ActionCategory.DATA_CLEANSING;

/**
 * Abstract class used as base class for delete actions.
 */
public abstract class AbstractDelete extends ActionMetadataAdapter implements ColumnAction {

    /**
     * @see ActionMetadata#getCategory()
     */
    @Override
    public String getCategory() {
        return DATA_CLEANSING.getDisplayName();
    }

    /**
     * Return true if the given value should be deleted.
     *
     *
     * @param context
     * @param value            the value to delete.
     * @return true if the given value should be deleted.
     */
    public abstract boolean toDelete(ActionContext context, final String value);

    /**
     * @see ColumnAction#applyOnColumn(DataSetRow, ActionContext)
     */
    @Override
    public void applyOnColumn(DataSetRow row, ActionContext context) {
        final String columnId = context.getColumnId();
        final String value = row.get(columnId);
        if (toDelete(context, value)) {
            row.setDeleted(true);
        }
    }

    @Override
    public Set<Behavior> getBehavior() {
        return EnumSet.of(Behavior.VALUES_ALL);
    }
}
