package org.talend.dataprep.transformation.api.action.metadata;

import static org.talend.dataprep.api.preparation.Action.Builder.builder;

import java.util.Iterator;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.api.preparation.Action;
import org.talend.dataprep.api.type.Type;
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
            String originalValue = row.get(columnId);
            final RowMetadata rowMetadata = context.getTransformedRowMetadata();
            final Iterator<ColumnMetadata> iterator = rowMetadata.getColumns().iterator();
            while (iterator.hasNext()) {
                if (columnId.equals(iterator.next().getId())) {
                    break;
                }
            }
            if (iterator.hasNext()) {
                row.set(iterator.next().getId(), originalValue);
            }
        }).withMetadata((rowMetadata, context) -> {
            String columnId = parameters.get(COLUMN_ID);
            final ColumnMetadata column = rowMetadata.getById(columnId);
            ColumnMetadata newColumnMetadata = ColumnMetadata.Builder //
                    .column() //
                    .name(column.getName() + COPY_APPENDIX) //
                    .type(Type.get(column.getType())) //
                    .headerSize(column.getHeaderSize()) //
                    .build();
            rowMetadata.insertAfter(columnId, newColumnMetadata);
        }).build();
    }
}
