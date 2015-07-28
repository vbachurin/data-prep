package org.talend.dataprep.transformation.api.action.metadata;

import static org.talend.dataprep.api.preparation.Action.Builder.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
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
            String columnName = parameters.get(COLUMN_ID);
            String originalValue = row.get(columnName);
            if (originalValue != null) {
                row.set(columnName + COPY_APPENDIX, originalValue);
            }
        }).withMetadata((rowMetadata, context) -> {

            String columnId = parameters.get(COLUMN_ID);

            List<ColumnMetadata> newColumns = new ArrayList<>(rowMetadata.size() + 1);

            for (ColumnMetadata column : rowMetadata.getColumns()) {
                ColumnMetadata newColumnMetadata = ColumnMetadata.Builder.column().copy(column).build();
                newColumns.add(newColumnMetadata);

                // append the split column
                if (StringUtils.equals(columnId, column.getId())) {
                    newColumnMetadata = ColumnMetadata.Builder //
                            .column() //
                            .computedId(column.getId() + COPY_APPENDIX) //
                            .name(column.getName() + COPY_APPENDIX) //
                            .type(Type.get(column.getType())) //
                            .empty(column.getQuality().getEmpty()) //
                            .invalid(column.getQuality().getInvalid()) //
                            .valid(column.getQuality().getValid()) //
                            .headerSize(column.getHeaderSize()) //
                            .build();
                    newColumnMetadata.setDomain( column.getDomain() );
                    newColumnMetadata.setDomainCount( column.getDomainCount() );
                    newColumnMetadata.setDomainLabel( column.getDomainLabel() );
                    newColumnMetadata.setSemanticDomains( column.getSemanticDomains() );
                    newColumns.add(newColumnMetadata);
                }

            }

            // apply the new columns to the row metadata
            rowMetadata.setColumns(newColumns);
        }).build();
    }
}
