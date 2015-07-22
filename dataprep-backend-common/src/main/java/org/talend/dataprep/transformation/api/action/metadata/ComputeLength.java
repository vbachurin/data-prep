package org.talend.dataprep.transformation.api.action.metadata;

import static org.talend.dataprep.api.preparation.Action.Builder.builder;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.api.preparation.Action;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.metadata.category.ActionCategory;
import org.talend.dataprep.transformation.api.action.metadata.common.SingleColumnAction;

@Component(ComputeLength.ACTION_BEAN_PREFIX + ComputeLength.LENGTH_ACTION_NAME)
public class ComputeLength extends SingleColumnAction {

    /**
     * The action name.
     */
    public static final String LENGTH_ACTION_NAME = "compute_length"; //$NON-NLS-1$

    /**
     * The column appendix.
     */
    public static final String APPENDIX = "_length"; //$NON-NLS-1$

    /**
     * @see ActionMetadata#getName()
     */
    @Override
    public String getName() {
        return LENGTH_ACTION_NAME;
    }

    /**
     * @see ActionMetadata#getCategory()
     */
    @Override
    public String getCategory() {
        return ActionCategory.QUICKFIX.getDisplayName();
    }

    /**
     * @see ActionMetadata#create(Map)
     */
    @Override
    public Action create(Map<String, String> parameters) {
        return builder().withRow((row, context) -> {
            String columnId = parameters.get(COLUMN_ID);
            final RowMetadata rowMetadata = row.getRowMetadata();
            final ColumnMetadata column = rowMetadata.getById(columnId);
            // Metadata actions
            // create the new column
            ColumnMetadata newColumnMetadata = ColumnMetadata.Builder //
                    .column() //
                    .name(column.getName() + APPENDIX) //
                    .type(Type.INTEGER) //
                    .empty(column.getQuality().getEmpty()) //
                    .invalid(column.getQuality().getInvalid()) //
                    .valid(column.getQuality().getValid()) //
                    .headerSize(column.getHeaderSize()) //
                    .build();
            // add the new column after the current one
            final String lengthColumn = rowMetadata.insertAfter(columnId, newColumnMetadata);
            // Set length value
            String value = row.get(columnId);
            if (value != null) {
                row.set(lengthColumn, String.valueOf(value.length()));
            } else {
                row.set(lengthColumn, "0");
            }
            return row;
        }).build();
    }

    /**
     * @see ActionMetadata#accept(ColumnMetadata)
     */
    @Override
    public boolean accept(ColumnMetadata column) {
        return Type.STRING.equals(Type.get(column.getType()));
    }
}
