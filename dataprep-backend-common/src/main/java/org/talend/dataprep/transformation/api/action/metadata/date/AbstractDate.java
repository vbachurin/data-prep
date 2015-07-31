package org.talend.dataprep.transformation.api.action.metadata.date;

import static org.talend.dataprep.api.type.Type.DATE;

import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.metadata.category.ActionCategory;
import org.talend.dataprep.transformation.api.action.metadata.common.SingleColumnAction;
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum;

public abstract class AbstractDate extends SingleColumnAction {

    /**
     * @see org.talend.dataprep.transformation.api.action.metadata.ActionMetadata#getCategory()
     */
    @Override
    public String getCategory() {
        return ActionCategory.DATE.getDisplayName();
    }

    /**
     * Only works on 'date' columns.
     *
     * @see org.talend.dataprep.transformation.api.action.metadata.ActionMetadata#accept(ColumnMetadata)
     */
    @Override
    public boolean accept(ColumnMetadata column) {
        final String domain = column.getDomain().toUpperCase();
        return DATE.equals(Type.get(column.getType())) || SemanticCategoryEnum.DATE.name().equals(domain);
    }
}
