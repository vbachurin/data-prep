package org.talend.dataprep.transformation.actions.delete;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.row.DataSetRow;
import org.talend.dataprep.transformation.actions.common.AbstractActionMetadata;
import org.talend.dataprep.transformation.actions.common.DataSetAction;
import org.talend.dataprep.transformation.api.action.context.ActionContext;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static org.talend.dataprep.transformation.actions.category.ActionCategory.DATA_CLEANSING;

/**
 * This table scope action delete all rows where all cells/values are empty.
 */
@Component(AbstractActionMetadata.ACTION_BEAN_PREFIX + DeleteEmptyRows.ACTION_NAME)
public class DeleteEmptyRows extends AbstractActionMetadata implements DataSetAction {

    protected static final String ACTION_NAME = "delete_empty_rows";

    @Override
    public String getName() {
        return ACTION_NAME;
    }

    @Override
    public String getCategory() {
        return DATA_CLEANSING.getDisplayName();
    }

    @Override
    public boolean acceptColumn(ColumnMetadata column) {
        return true;
    }

    @Override
    public Set<Behavior> getBehavior() {
        return EnumSet.of(Behavior.VALUES_ALL);
    }

    @Override
    public void applyOnDataSet(DataSetRow row, ActionContext context) {
        final List<ColumnMetadata> columns = row.getRowMetadata().getColumns();

        for (ColumnMetadata currentColumn : columns) {
            final String currentColumnId = currentColumn.getId();
            if (!StringUtils.isBlank(row.get(currentColumnId))) {
                // At least one value is not empty in this row. Nothing to do. Just return to loop on next row.
                return;
            }
        }

        // All values are empty in this row: delete it!
        row.setDeleted(true);
    }
}
