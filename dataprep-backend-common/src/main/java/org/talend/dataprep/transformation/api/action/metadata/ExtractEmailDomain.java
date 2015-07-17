package org.talend.dataprep.transformation.api.action.metadata;

import static org.talend.dataprep.api.preparation.Action.Builder.builder;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.api.preparation.Action;
import org.talend.dataprep.api.type.Type;

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
        return ActionCategory.QUICKFIX.getDisplayName();
    }

    /**
     * @see ActionMetadata#accept(ColumnMetadata)
     */
    @Override
    public boolean accept(ColumnMetadata column) {
        if (!Type.STRING.equals(Type.get(column.getType()))) {
            return false;
        }
        return StringUtils.equalsIgnoreCase("email", column.getDomain());
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
            final RowMetadata rowMetadata = row.getRowMetadata();
            final ColumnMetadata column = rowMetadata.getById(columnId);
            // Perform metadata level actions (add local + domain columns).
            ColumnMetadata.Builder newColumnMetadata = ColumnMetadata.Builder //
                    .column() //
                    .name(column.getName() + _LOCAL) //
                    .type(Type.get(column.getType())) //
                    .headerSize(column.getHeaderSize());
            String local = rowMetadata.insertAfter(columnId, newColumnMetadata.build());
            newColumnMetadata = ColumnMetadata.Builder //
                    .column() //
                    .name(column.getName() + _DOMAIN) //
                    .type(Type.get(column.getType())) //
                    .empty(column.getQuality().getEmpty()) //
                    .invalid(column.getQuality().getInvalid()) //
                    .valid(column.getQuality().getValid()) //
                    .headerSize(column.getHeaderSize());
            String domain = rowMetadata.insertAfter(local, newColumnMetadata.build());
            // Set the values in newly created columns
            if (originalValue == null) {
                return row;
            }
            String[] split = originalValue.split("@", 2);
            String local_part = split.length >= 2 ? split[0] : StringUtils.EMPTY;
            row.set(local, local_part);
            String domain_part = split.length >= 2 ? split[1] : StringUtils.EMPTY;
            row.set(domain, domain_part);
            return row;
        }).build();
    }
}
