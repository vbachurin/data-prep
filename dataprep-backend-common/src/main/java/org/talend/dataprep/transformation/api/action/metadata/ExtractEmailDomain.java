package org.talend.dataprep.transformation.api.action.metadata;

import java.util.*;
import java.util.function.Consumer;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.parameters.Item;

/**
 * Split a cell value on a separator.
 */
@Component(ExtractEmailDomain.ACTION_BEAN_PREFIX + ExtractEmailDomain.EXTRACT_DOMAIN_ACTION_NAME)
public class ExtractEmailDomain extends SingleColumnAction {

    /** The action name. */
    public static final String EXTRACT_DOMAIN_ACTION_NAME = "extractemaildomain"; //$NON-NLS-1$

    /**
     * Private constructor to ensure IoC use.
     */
    protected ExtractEmailDomain() {
    }

    /**
     * @see ActionMetadata#getName()
     */
    @Override
    public String getName() {
        return EXTRACT_DOMAIN_ACTION_NAME;
    }

    /**
     * @see ActionMetadata#getCategory()
     */
    @Override
    public String getCategory() {
        return "columns"; //$NON-NLS-1$
    }

    /**
     * @see ActionMetadata#getCompatibleColumnTypes()
     */
    @Override
    public boolean accept(ColumnMetadata column) {
        return Type.STRING.equals(Type.get(column.getType()));
    }

    /**
     * @see ActionMetadata#getItems()@return
     */
    @Override
    public Item[] getItems() {
        return new Item[] {};
    }

    /**
     * Split the column for each row.
     *
     * @see ActionMetadata#create(Map)
     */
    @Override
    public Consumer<DataSetRow> create(Map<String, String> parameters) {
        String columnName = parameters.get(COLUMN_ID);
        String realSeparator = "@";

        return row -> {
            String originalValue = row.get(columnName);
            if (originalValue != null) {
                String[] split = originalValue.split(realSeparator, 2);

                String local_part = (split.length >= 2 ? split[0] : StringUtils.EMPTY);
                row.set(columnName + "_local", local_part);

                String domain_part = (split.length >= 2 ? split[1] : StringUtils.EMPTY);
                row.set(columnName + "_domain", domain_part);
            }
        };
    }

    /**
     * Update row metadata.
     *
     * @see ActionMetadata#createMetadataClosure(Map)
     */
    @Override
    public Consumer<RowMetadata> createMetadataClosure(Map<String, String> parameters) {

        return rowMetadata -> {

            String columnId = parameters.get(COLUMN_ID);

            List<ColumnMetadata> newColumns = new ArrayList<>(rowMetadata.size() + 1);

            for (ColumnMetadata column : rowMetadata.getColumns()) {
                ColumnMetadata newColumnMetadata = ColumnMetadata.Builder.column().copy(column).build();
                newColumns.add(newColumnMetadata);

                // append the split column
                if (StringUtils.equals(columnId, column.getId())) {
                    newColumnMetadata = ColumnMetadata.Builder //
                            .column() //
                            .computedId(column.getId() + "_local") //
                            .name(column.getName() + "_local") //
                            .type(Type.get(column.getType())) //
                            .empty(column.getQuality().getEmpty()) //
                            .invalid(column.getQuality().getInvalid()) //
                            .valid(column.getQuality().getValid()) //
                            .headerSize(column.getHeaderSize()) //
                            .build();
                    newColumns.add(newColumnMetadata);

                    newColumnMetadata = ColumnMetadata.Builder //
                            .column() //
                            .computedId(column.getId() + "_domain") //
                            .name(column.getName() + "_domain") //
                            .type(Type.get(column.getType())) //
                            .empty(column.getQuality().getEmpty()) //
                            .invalid(column.getQuality().getInvalid()) //
                            .valid(column.getQuality().getValid()) //
                            .headerSize(column.getHeaderSize()) //
                            .build();
                    newColumns.add(newColumnMetadata);
                }

            }

            // apply the new columns to the row metadata
            rowMetadata.setColumns(newColumns);
        };
    }
}
