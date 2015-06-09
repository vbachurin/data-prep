package org.talend.dataprep.transformation.api.action.metadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;

/**
 * Split a cell value on a separator.
 */
@Component(ExtractEmailDomain.ACTION_BEAN_PREFIX + ExtractEmailDomain.EXTRACT_DOMAIN_ACTION_NAME)
public class ExtractEmailDomain extends SingleColumnAction {

    /** The action name. */
    public static final String EXTRACT_DOMAIN_ACTION_NAME = "extractemaildomain"; //$NON-NLS-1$

    /** The local suffix. */
    private static final String _LOCAL = "_local"; //$NON-NLS-1$

    /** The domain suffix. */
    private static final String _DOMAIN = "_domain"; //$NON-NLS-1$

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
        return ActionCategory.COLUMNS.getDisplayName();
    }

    /**
     * @see ActionMetadata#accept(ColumnMetadata)
     */
    @Override
    public boolean accept(ColumnMetadata column) {
        return Type.STRING.equals(Type.get(column.getType()));
    }

    /**
     * Split the column for each row.
     *
     * @see ActionMetadata#create(Map)
     */
    @Override
    public BiConsumer<DataSetRow, TransformationContext> create(Map<String, String> parameters) {

        String columnName = parameters.get(COLUMN_ID);
        String realSeparator = "@";

        return (row, context) -> {
            String originalValue = row.get(columnName);
            if (originalValue != null) {
                String[] split = originalValue.split(realSeparator, 2);

                String local_part = split.length >= 2 ? split[0] : StringUtils.EMPTY;
                row.set(columnName + _LOCAL, local_part);

                String domain_part = split.length >= 2 ? split[1] : StringUtils.EMPTY;
                row.set(columnName + _DOMAIN, domain_part);
            }
        };
    }

    /**
     * Update row metadata.
     *
     * @see ActionMetadata#createMetadataClosure(Map)
     */
    @Override
    public BiConsumer<RowMetadata, TransformationContext> createMetadataClosure(Map<String, String> parameters) {

        return (rowMetadata, context) -> {

            String columnId = parameters.get(COLUMN_ID);

            List<ColumnMetadata> newColumns = new ArrayList<>(rowMetadata.size() + 1);

            for (ColumnMetadata column : rowMetadata.getColumns()) {
                ColumnMetadata newColumnMetadata = ColumnMetadata.Builder.column().copy(column).build();
                newColumns.add(newColumnMetadata);

                // append the split column
                if (StringUtils.equals(columnId, column.getId())) {
                    newColumnMetadata = ColumnMetadata.Builder //
                            .column() //
                            .computedId(column.getId() + _LOCAL) //
                            .name(column.getName() + _LOCAL) //
                            .type(Type.get(column.getType())) //
                            .empty(column.getQuality().getEmpty()) //
                            .invalid(column.getQuality().getInvalid()) //
                            .valid(column.getQuality().getValid()) //
                            .headerSize(column.getHeaderSize()) //
                            .build();
                    newColumns.add(newColumnMetadata);

                    newColumnMetadata = ColumnMetadata.Builder //
                            .column() //
                            .computedId(column.getId() + _DOMAIN) //
                            .name(column.getName() + _DOMAIN) //
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
