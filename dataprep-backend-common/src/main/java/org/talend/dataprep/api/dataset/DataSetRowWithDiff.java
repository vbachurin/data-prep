package org.talend.dataprep.api.dataset;

import java.util.HashMap;
import java.util.Map;

/**
 * DataSetRow with diff mechanism enabled.
 */
public class DataSetRowWithDiff extends DataSetRow {

    /** Flag used for the diff. */
    public enum FLAG {
        DELETE("delete"),
        NEW("new"),
        UPDATE("update");

        /** Expected frontend value. */
        private String value;

        /**
         * Constructor with the given value.
         * 
         * @param value the expected frontend value.
         */
        FLAG(String value) {
            this.value = value;
        }

        /**
         * @return the frontend expected value.
         */
        public String getValue() {
            return value;
        }
    }

    /** Key to put in the result values that tells that there are differences at column level. */
    public final static String DIFF_KEY = "__tdpDiff";

    /** Key to put in the result values that tells that there is a difference at row level. */
    public final static String ROW_DIFF_KEY = "__tdpRowDiff";

    /** The dataset row reference to compute the diff from. */
    private DataSetRow reference;

    /** Hashmap that holds the diff flags. */
    private Map<String, String> diffFlags;

    /** Diff flag for the row. */
    private FLAG rowFlag;

    /**
     * Create a dataset for the given row with the other one as reference.
     * 
     * @param row the row to 'copy'.
     * @param reference the row reference.
     */
    public DataSetRowWithDiff(DataSetRow row, DataSetRow reference) {
        super(row.values);
        this.setDeleted(row.isDeleted());
        this.reference = reference;
        this.diffFlags = new HashMap<>();
        this.rowFlag = null;
    }

    /**
     * Get the value associated with the provided key
     * 
     * @param name the key.
     * @return the value as string.
     */
    public String get(final String name) {
        return values.get(name);
    }

    /**
     * @return the reference row for the diff.
     */
    public DataSetRow getReference() {
        return reference;
    }

    /**
     * @see DataSetRow#values()
     */
    @Override
    public Map<String, Object> values() {
        Map<String, Object> values = super.values();
        if (!diffFlags.isEmpty()) {
            values.put(DIFF_KEY, diffFlags);
        }
        if (rowFlag != null) {
            values.put(ROW_DIFF_KEY, rowFlag.getValue());
        }
        return values;
    }

    /**
     * Clear all values in this row and reset state as it was when created (e.g. {@link #isDeleted()} returns
     * <code>false</code>).
     */
    public void clear() {
        setDeleted(false);
        values.clear();
        reference = null;
        diffFlags.clear();
    }

    /**
     * @see Cloneable#clone()
     */
    @Override
    public DataSetRowWithDiff clone() {
        return new DataSetRowWithDiff(this, this.reference);
    }

    /**
     * Determine if the row should be written
     */
    public boolean shouldWrite() {
        if (this.reference == null) {
            return !isDeleted();
        } else {
            return !reference.isDeleted() || !isDeleted();
        }
    }

    /**
     * Set the given flag for the given column
     *
     * @param flag the flag to set.
     * @param columnName the column name to set the flag to.
     */
    public void setFlag(FLAG flag, String columnName) {
        diffFlags.put(columnName, flag.getValue());
    }

    /**
     * Clear any flag one the given column
     *
     * @param columnName the column name to clear the flag to.
     */
    public void clearFlag(String columnName) {
        diffFlags.remove(columnName);
    }

    /**
     * @param flag the row flag to set.
     */
    public void setRowFlag(FLAG flag) {
        this.rowFlag = flag;
    }

    /**
     * Clear the row flag.
     */
    public void clearRowFlag() {
        this.rowFlag = null;
    }

}
