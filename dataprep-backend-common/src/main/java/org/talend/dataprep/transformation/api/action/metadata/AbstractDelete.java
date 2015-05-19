package org.talend.dataprep.transformation.api.action.metadata;

import static org.talend.dataprep.api.dataset.DataSetRowWithDiff.FLAG.DELETE;
import static org.talend.dataprep.api.dataset.DataSetRowWithDiff.FLAG.NEW;

import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.lang.StringUtils;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.DataSetRowWithDiff;
import org.talend.dataprep.api.type.Type;

/**
 * Abstract class used as base class for delete actions.
 */
public abstract class AbstractDelete implements ActionMetadata {

    /** Name of the column to delete parameter. */
    public static final String COLUMN_NAME_PARAMETER = "column_name"; //$NON-NLS-1$

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

            // compute diff flags if needed
            if (!(row instanceof DataSetRowWithDiff)) {
                return;
            }

            DataSetRowWithDiff rowWithDiff = (DataSetRowWithDiff) row;
            DataSetRow reference = rowWithDiff.getReference();

            // row is no more deleted : we write row values with the *NEW* flag
            if (reference.isDeleted() && !rowWithDiff.isDeleted()) {
                rowWithDiff.setRowFlag(NEW);
            }
            // row has been deleted : we write row values with the *DELETED* flag
            else if (!reference.isDeleted() && rowWithDiff.isDeleted()) {
                rowWithDiff.setRowFlag(DELETE);
            }
            // row is in the same state as the reference one, let's clear the flag
            else {
                rowWithDiff.clearRowFlag();
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
