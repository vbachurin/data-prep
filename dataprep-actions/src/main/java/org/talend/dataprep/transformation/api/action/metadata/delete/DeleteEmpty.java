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

import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.transformation.api.action.context.ActionContext;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadataAdapter;
import org.talend.dataprep.transformation.api.action.metadata.common.ColumnAction;

import java.util.Collections;
import java.util.List;

import static org.talend.dataprep.transformation.api.action.metadata.category.ActionScope.EMPTY;

/**
 * Delete row when value is empty.
 */
@Component(ActionMetadataAdapter.ACTION_BEAN_PREFIX + DeleteEmpty.DELETE_EMPTY_ACTION_NAME)
public class DeleteEmpty extends AbstractDelete implements ColumnAction {

    /**
     * The action name.
     */
    public static final String DELETE_EMPTY_ACTION_NAME = "delete_empty"; //$NON-NLS-1$

    /**
     * @see ActionMetadata#getName()
     */
    @Override
    public String getName() {
        return DELETE_EMPTY_ACTION_NAME;
    }

    /**
     * @see ActionMetadata#getActionScope()
     */
    @Override
    public List<String> getActionScope() {
        return Collections.singletonList(EMPTY.getDisplayName());
    }

    /**
     * @see ActionMetadata#acceptColumn(ColumnMetadata)
     */
    @Override
    public boolean acceptColumn(ColumnMetadata column) {
        return true;
    }

    @Override
    public void compile(ActionContext actionContext) {
        // This action is able to deal with missing column, overrides default behavior
        actionContext.setActionStatus(ActionContext.ActionStatus.OK);
    }

    /**
     * @see AbstractDelete#toDelete(ActionContext, String)
     */
    @Override
    public boolean toDelete(ActionContext context, final String value) {
        return value == null || value.trim().length() == 0;
    }
}
