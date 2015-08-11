package org.talend.dataprep.transformation.api.action.metadata.delete;

import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;

/**
 * Delete row when value is invalid.
 */
@Component(DeleteInvalid.ACTION_BEAN_PREFIX + DeleteInvalid.DELETE_INVALID_ACTION_NAME)
public class DeleteInvalid extends AbstractDelete {

    /**
     * The action name.
     */
    public static final String DELETE_INVALID_ACTION_NAME = "delete_invalid"; //$NON-NLS-1$

    /**
     * @see ActionMetadata#getName()
     */
    @Override
    public String getName() {
        return DELETE_INVALID_ACTION_NAME;
    }

    /**
     * @see ActionMetadata#acceptColumn(ColumnMetadata)
     */
    @Override
    public boolean acceptColumn(ColumnMetadata column) {
        return true;
    }

    @Override
    public boolean toDelete(ColumnMetadata colMetadata, Map<String, String> parsedParameters, String value) {
        final Set<String> invalidValues = colMetadata.getQuality().getInvalidValues();
        return invalidValues.contains(value);
    }
}
