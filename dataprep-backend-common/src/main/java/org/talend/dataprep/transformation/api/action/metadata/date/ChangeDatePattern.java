package org.talend.dataprep.transformation.api.action.metadata.date;

import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.metadata.ActionCategory;
import org.talend.dataprep.transformation.api.action.metadata.ActionMetadata;
import org.talend.dataprep.transformation.api.action.metadata.SingleColumnAction;

/**
 * Change the date pattern on a 'date' column.
 */
public class ChangeDatePattern extends SingleColumnAction {

    /** Action name. */
    public static final String CHANGE_DATE_PATTERN_ACTION_NAME = "change_date_pattern"; //$NON-NLS-1$

    /** Name of the new date pattern parameter. */
    private static final String NEW_DATE_PATTERN_PARAMETER_NAME = "new_date_pattern"; //$NON-NLS-1$

    /**
     * @see ActionMetadata#getName()
     */
    @Override
    public String getName() {
        return CHANGE_DATE_PATTERN_ACTION_NAME;
    }

    /**
     * @see ActionMetadata#getCategory()
     */
    @Override
    public String getCategory() {
        return ActionCategory.DATE.getDisplayName();
    }

    /**
     * Only works on 'date' columns.
     * 
     * @see ActionMetadata#accept(ColumnMetadata)
     */
    @Override
    public boolean accept(ColumnMetadata column) {
        return Type.DATE.equals(Type.get(column.getType()));
    }
}
