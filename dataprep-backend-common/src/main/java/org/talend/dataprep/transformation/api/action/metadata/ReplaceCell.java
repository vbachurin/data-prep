package org.talend.dataprep.transformation.api.action.metadata;

import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.preparation.Action;
import org.talend.dataprep.transformation.api.action.metadata.common.CellAction;

import java.util.Map;

import static org.talend.dataprep.transformation.api.action.metadata.category.ActionCategory.QUICKFIX;

/**
 * Replace a specific cell value
 */
//@Component(ReplaceCell.ACTION_BEAN_PREFIX + ReplaceCell.REPLACE_CELL_ACTION_NAME)
public class ReplaceCell extends CellAction {
    public static final String REPLACE_CELL_ACTION_NAME = "replace_cell"; //$NON-NLS-1$

    @Override
    public String getName() {
        return REPLACE_CELL_ACTION_NAME;
    }

    @Override
    public String getCategory() {
        return QUICKFIX.getDisplayName();
    }

    @Override
    public boolean accept(ColumnMetadata column) {
        return false;
    }

    @Override
    public Action create(Map<String, String> parameters) {
        return null;
    }
}
