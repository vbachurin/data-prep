package org.talend.dataprep.transformation.api.action.metadata.column;

import static org.talend.dataprep.api.dataset.ColumnMetadata.Builder.column;

import java.util.Map;

import javax.annotation.Nonnull;

import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;
import org.talend.dataprep.transformation.api.action.metadata.category.ActionCategory;
import org.talend.dataprep.transformation.api.action.metadata.common.AbstractActionMetadata;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;
import org.talend.dataprep.transformation.api.action.metadata.common.ColumnAction;
import org.talend.dataprep.transformation.api.action.parameters.Item;

/**
 * duplicate a column
 */
@Component(CopyColumnMetadata.ACTION_BEAN_PREFIX + CopyColumnMetadata.COPY_ACTION_NAME)
public class CopyColumnMetadata extends AbstractActionMetadata implements ColumnAction {

    /**
     * The action name.
     */
    public static final String COPY_ACTION_NAME = "copy"; //$NON-NLS-1$

    /**
     * The split column appendix.
     */
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
        return new Item[]{};
    }

    /**
     * @see ActionMetadata#acceptColumn(ColumnMetadata)
     */
    @Override
    public boolean acceptColumn(ColumnMetadata column) {
        return true;
    }

    /**
     * @see ColumnAction#applyOnColumn(DataSetRow, TransformationContext, Map, String)
     */
    @Override
    public void applyOnColumn(DataSetRow row, TransformationContext context, Map<String, String> parameters, String columnId) {
        final RowMetadata rowMetadata = row.getRowMetadata();
        final ColumnMetadata column = rowMetadata.getById(columnId);
        final String copyColumn = context.in(this).column(
                column.getName() + COPY_APPENDIX,
                () -> column().copy(column).name(column.getName() + COPY_APPENDIX).build(),
                (c) -> rowMetadata.insertAfter(columnId, c)
        );
        row.set(copyColumn, row.get(columnId));
    }
}
