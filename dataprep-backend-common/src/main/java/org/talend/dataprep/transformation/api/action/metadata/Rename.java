package org.talend.dataprep.transformation.api.action.metadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.api.type.Type;
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
        // TODO define category
        return "columns";
    }

    /**
     * @see ActionMetadata#getParameters()
     */
    @Override
    @Nonnull
    public Parameter[] getParameters() {
        return new Parameter[] { COLUMN_ID_PARAMETER, COLUMN_NAME_PARAMETER,
                new Parameter(NEW_COLUMN_NAME_PARAMETER_NAME, Type.STRING.getName(), StringUtils.EMPTY) };
    }

    /**
     * @see ActionMetadata#accept(ColumnMetadata)
     */
    @Override
    public boolean accept(ColumnMetadata column) {
        // allow on all columns
        return true;
    }

    /**
     * Update the row metadata.
     * 
     * @see ActionMetadata#createMetadataClosure(Map)
     */
    @Override
    public Consumer<RowMetadata> createMetadataClosure(Map<String, String> parameters) {

        return rowMetadata -> {

            String columnId = parameters.get(COLUMN_ID);
            String newColumnName = parameters.get(NEW_COLUMN_NAME_PARAMETER_NAME);

            List<ColumnMetadata> newColumns = new ArrayList<>(rowMetadata.size());

            for (ColumnMetadata column : rowMetadata.getColumns()) {
                ColumnMetadata newColumnMetadata;
                // rename the column
                if (StringUtils.equals(columnId, column.getId())) {
                    newColumnMetadata = ColumnMetadata.Builder.column() //
                            .computedId(column.getId()) //
                            .name(newColumnName) // new name
                            .type(Type.get(column.getType())) //
                            .empty(column.getQuality().getEmpty()) //
                            .invalid(column.getQuality().getInvalid()) //
                            .valid(column.getQuality().getValid()) //
                            .headerSize(column.getHeaderSize()) //
                            .build();
                }
                // add a copy of the column
                else {
                    newColumnMetadata = ColumnMetadata.Builder.column().copy(column).build();
                }
                newColumns.add(newColumnMetadata);
            }

            rowMetadata.setColumns(newColumns);
        };
    }

}
