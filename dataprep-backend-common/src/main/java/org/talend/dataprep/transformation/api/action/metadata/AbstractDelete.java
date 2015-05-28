package org.talend.dataprep.transformation.api.action.metadata;

import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.lang.StringUtils;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.parameters.Item;
import org.talend.dataprep.transformation.api.action.parameters.Parameter;

/**
 * Abstract class used as base class for delete actions.
 */
public abstract class AbstractDelete implements ActionMetadata {

    /** Name of the column to delete parameter. */
    public static final String COLUMN_NAME_PARAMETER = "column_id"; //$NON-NLS-1$

    /**
     * @see ActionMetadata#getCategory()
     */
    @Override
    public String getCategory() {
        return "cleansing"; //$NON-NLS-1$
    }

    /**
     * @see ActionMetadata#getItems()
     */
    @Override
    public Item[] getItems() {
        return new Item[0];
    }

    /**
     * @see ActionMetadata#getParameters()
     */
    @Override
    public Parameter[] getParameters() {
        return new Parameter[] { new Parameter(COLUMN_NAME_PARAMETER, Type.STRING.getName(), StringUtils.EMPTY) };
    }

    /**
     * @see ActionMetadata#create(Map)
     */
    @Override
    public Consumer<DataSetRow> create(Map<String, String> parameters) {
        return row -> {
            String columnName = parameters.get(COLUMN_NAME_PARAMETER);
            String value = row.get(columnName);
            if (toDelete(parameters, value)) {
                row.setDeleted(true);
            }
        };
    }

    /**
     * Return true if the given value should be deleted.
     * 
     * @param parsedParameters the delete action parameters.
     * @param value the value to delete.
     * @return true if the given value should be deleted.
     */
    public abstract boolean toDelete(Map<String, String> parsedParameters, String value);

}
