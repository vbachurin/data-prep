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

package org.talend.dataprep.transformation.api.action.metadata.column;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.transformation.api.action.context.ActionContext;
import org.talend.dataprep.transformation.api.action.metadata.category.ActionCategory;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadataAdapter;
import org.talend.dataprep.transformation.api.action.metadata.common.ColumnAction;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static org.talend.dataprep.transformation.api.action.metadata.category.ActionScope.COLUMN_METADATA;

/**
 * Deletes a column from a dataset. This action is available from column headers</b>
 */
@Component(ActionMetadataAdapter.ACTION_BEAN_PREFIX + DeleteColumn.DELETE_COLUMN_ACTION_NAME)
public class DeleteColumn extends ActionMetadataAdapter implements ColumnAction {

    /**
     * The action name.
     */
    public static final String DELETE_COLUMN_ACTION_NAME = "delete_column"; //$NON-NLS-1$

    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteColumn.class);

    /**
     * @see ActionMetadata#getName()
     */
    @Override
    public String getName() {
        return DELETE_COLUMN_ACTION_NAME;
    }

    /**
     * @see ActionMetadata#acceptColumn(ColumnMetadata)
     */
    @Override
    public boolean acceptColumn(ColumnMetadata column) {
        return true;
    }

    /**
     * @see ActionMetadata#getCategory()
     */
    @Override
    public String getCategory() {
        return ActionCategory.COLUMN_METADATA.getDisplayName();
    }


    /**
     * @see ActionMetadata#getActionScope()
     */
    @Override
    public List<String> getActionScope() {
        return Collections.singletonList(COLUMN_METADATA.getDisplayName());
    }

    /**
     * @see ColumnAction#applyOnColumn(DataSetRow, ActionContext)
     */
    @Override
    public void applyOnColumn(DataSetRow row, ActionContext context) {
        final String columnId = context.getColumnId();
        LOGGER.debug("DeleteColumn for columnId {}", columnId);
        context.getRowMetadata().deleteColumnById(columnId);
        row.deleteColumnById(columnId);
    }

    @Override
    public Set<Behavior> getBehavior() {
        return EnumSet.of(Behavior.METADATA_DELETE_COLUMNS);
    }

}
