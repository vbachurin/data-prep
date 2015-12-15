package org.talend.dataprep.transformation.api.action.metadata.line;

import static org.talend.dataprep.transformation.api.action.metadata.category.ActionCategory.DATA_CLEANSING;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.transformation.api.action.context.ActionContext;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;
import org.talend.dataprep.transformation.api.action.metadata.common.RowAction;

/**
 * This action do two things:
 * <ul>
 * <li>Take the value in each column of this row, and use them as column names</li>
 * <li>Delete this row</li>
 * </ul>
 */
@Component(MakeLineHeader.ACTION_BEAN_PREFIX + MakeLineHeader.ACTION_NAME)
public class MakeLineHeader extends ActionMetadata implements RowAction {

    public static final String ACTION_NAME = "make_line_header";

    private static final Logger LOGGER = LoggerFactory.getLogger(MakeLineHeader.class);

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
    protected boolean implicitFilter() {
        return false;
    }

    @Override
    public void applyOnLine(DataSetRow row, ActionContext context) {
        LOGGER.debug("Make line header for rowId {} with parameters {} ", context.getRowId(), context.getParameters());

        for (ColumnMetadata column : row.getRowMetadata().getColumns()) {
            column.setName(row.get(column.getId()));
        }
        context.setOutputRowMetadata(row.getRowMetadata().clone());
        if (getFilter(context.getParameters()).test(row)) {
            row.setDeleted(true);
            context.setActionStatus(ActionContext.ActionStatus.DONE);
        }
    }

}
