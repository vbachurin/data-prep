package org.talend.dataprep.transformation.api.action.metadata.delete;

import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.i18n.MessagesBundle;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;
import org.talend.dataprep.transformation.api.action.metadata.category.ScopeCategory;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;
import org.talend.dataprep.transformation.api.action.metadata.common.RowAction;

import java.util.Map;

import static org.talend.dataprep.transformation.api.action.metadata.category.ActionCategory.DATA_CLEANSING;
import static org.talend.dataprep.transformation.api.action.metadata.category.ScopeCategory.LINE;

@Component(Delete.ACTION_BEAN_PREFIX + Delete.DELETE_ACTION_NAME)
public class Delete extends ActionMetadata implements RowAction {

    public static final String DELETE_ACTION_NAME = "delete";
    public static final String DELETE_SINGLE_LINE = "delete_single_line";
    public static final String DELETE_COLUMN = "delete_column";
    private final ScopeCategory scope;

    public Delete() {
        this(LINE);
    }

    public Delete(final ScopeCategory scope) {
        this.scope = scope;
    }

    @Override
    public String getName() {
        return DELETE_ACTION_NAME;
    }

    @Override
    public String getDescription() {
        switch (scope) {
            case LINE:
                return MessagesBundle.getString("action." + DELETE_SINGLE_LINE + ".desc");
            case COLUMN:
                return MessagesBundle.getString("action." + DELETE_COLUMN + ".desc");
        }
        return null;
    }

    @Override
    public String getLabel() {
        switch (scope) {
            case LINE:
                return MessagesBundle.getString("action." + DELETE_SINGLE_LINE + ".label");
            case COLUMN:
                return MessagesBundle.getString("action." + DELETE_COLUMN + ".label");
        }
        return null;
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
    public void applyOnLine(DataSetRow row, TransformationContext context, Map<String, String> parameters, Long rowId) {
        if (row.getTdpId().equals(rowId)) {
            row.setDeleted(true);
        }
    }

    @Override
    public ActionMetadata adapt(ScopeCategory scope) {
        return new Delete(scope);
    }
}
