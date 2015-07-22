package org.talend.dataprep.transformation.api.action.metadata;

import static org.talend.dataprep.api.preparation.Action.Builder.builder;

import java.util.Map;

import javax.annotation.Nonnull;

import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.api.preparation.Action;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.metadata.category.ActionCategory;
import org.talend.dataprep.transformation.api.action.metadata.common.SingleColumnAction;
import org.talend.dataprep.transformation.api.action.parameters.Item;

/**
 * Split a cell value on a separator.
 */
@Component(CopyColumn.ACTION_BEAN_PREFIX + CopyColumn.COPY_ACTION_NAME)
public class CopyColumn extends SingleColumnAction {

    /** The action name. */
    public static final String COPY_ACTION_NAME = "copy"; //$NON-NLS-1$

    /** The split column appendix. */
    public static final String COPY_APPENDIX = "_copy"; //$NON-NLS-1$

    /**
     * @see ActionMetadata#getName()
     */
    @Override
    public String getName() {
        return COPY_ACTION_NAME;
    }

    /**
     * @see ActionMetadata#getCategory()
     */
    @Override
    public String getCategory() {
        return ActionCategory.COLUMNS.getDisplayName();
    }

    /**
     * @see ActionMetadata#getItems()@return
     */
    @Override
    @Nonnull
    public Item[] getItems() {
        return new Item[] {};
    }

    /**
     * @see ActionMetadata#accept(ColumnMetadata)
     */
    @Override
    public boolean accept(ColumnMetadata column) {
        return true;
    }

    /**
     * Split the column for each row.
     *
     * @see ActionMetadata#create(Map)
     */
    @Override
    public Action create(Map<String, String> parameters) {
        return builder().withRow((row, context) -> {
            String columnId = parameters.get(COLUMN_ID);
            final RowMetadata rowMetadata = row.getRowMetadata();
            final ColumnMetadata column = rowMetadata.getById(columnId);
            ColumnMetadata newColumnMetadata = ColumnMetadata.Builder //
                    .column() //
                    .name(column.getName() + COPY_APPENDIX) //
                    .type(Type.get(column.getType())) //
                    .headerSize(column.getHeaderSize()) //
                    .build();
            final String copyColumn = rowMetadata.insertAfter(columnId, newColumnMetadata);
            row.set(copyColumn, row.get(columnId));
            return row;
        }).build();
    }
}
