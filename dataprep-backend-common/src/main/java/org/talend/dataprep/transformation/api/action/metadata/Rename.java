package org.talend.dataprep.transformation.api.action.metadata;

import static org.talend.dataprep.api.preparation.Action.Builder.builder;

import java.util.Map;

import javax.annotation.Nonnull;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.api.preparation.Action;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.metadata.category.ActionCategory;
import org.talend.dataprep.transformation.api.action.metadata.common.SingleColumnAction;
import org.talend.dataprep.transformation.api.action.parameters.Parameter;

/**
 * Rename a column.
 *
 * If the column to rename does not exist or the new name is already used, nothing happen.
 */
@Component(Rename.ACTION_BEAN_PREFIX + Rename.RENAME_ACTION_NAME)
public class Rename extends SingleColumnAction {

    /** Action name. */
    public static final String RENAME_ACTION_NAME = "rename_column"; //$NON-NLS-1$

    /** Name of the new column parameter. */
    public static final String NEW_COLUMN_NAME_PARAMETER_NAME = "new_column_name"; //$NON-NLS-1$

    /** Parameters (column name, new column name...) */
    private final Parameter[] parameters;

    // Default parameters
    public Rename() {
        parameters = new Parameter[] { COLUMN_ID_PARAMETER, COLUMN_NAME_PARAMETER,
                new Parameter(NEW_COLUMN_NAME_PARAMETER_NAME, Type.STRING.getName(), StringUtils.EMPTY) };
    }

    // For overridden parameters
    public Rename(Parameter[] parameters) {
        this.parameters = parameters;
    }

    /**
     * @see ActionMetadata#getName()
     */
    @Override
    public String getName() {
        return RENAME_ACTION_NAME;
    }

    /**
     * @see ActionMetadata#getCategory()
     */
    @Override
    public String getCategory() {
        return ActionCategory.COLUMNS.getDisplayName();
    }

    /**
     * @see ActionMetadata#getParameters()
     */
    @Override
    @Nonnull
    public Parameter[] getParameters() {
        return parameters;
    }

    /**
     * @see ActionMetadata#accept(ColumnMetadata)
     */
    @Override
    public boolean accept(ColumnMetadata column) {
        // allow on all columns
        return true;
    }

    @Override
    public Action create(Map<String, String> parameters) {
        return builder().withRow((row, context) -> {
            String columnId = parameters.get(COLUMN_ID);
            String newColumnName = parameters.get(NEW_COLUMN_NAME_PARAMETER_NAME);
            final RowMetadata rowMetadata = row.getRowMetadata();
            final ColumnMetadata column = rowMetadata.getById(columnId);
            if (column != null) {
                column.setName(newColumnName);
            }
            return row;
        }).build();
    }

    /**
     * @param column A {@link ColumnMetadata column} information.
     * @return A rename action with <code>column</code> name as default name.
     */
    @Override
    public ActionMetadata adapt(ColumnMetadata column) {
        if (column == null) {
            return this;
        }
        final Parameter[] newParameters = {COLUMN_ID_PARAMETER, COLUMN_NAME_PARAMETER,
                new Parameter(NEW_COLUMN_NAME_PARAMETER_NAME, Type.STRING.getName(), column.getName())};
        return new Rename(newParameters);
    }
}
