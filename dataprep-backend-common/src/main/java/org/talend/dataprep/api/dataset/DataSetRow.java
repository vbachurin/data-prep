package org.talend.dataprep.api.dataset;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;

public class DataSetRow implements Cloneable {

    private final static String DIFF_KEY = "__tdpDiff";

    private final static String ROW_DIFF_KEY = "__tdpRowDiff";

    private final static String ROW_DIFF_DELETED = "delete";

    private final static String ROW_DIFF_NEW = "new";

    private final static String DIFF_UPDATE = "update";

    private boolean deleted = false;

    private DataSetRow oldRow;

    private final Map<String, String> values = new LinkedHashMap<>();

    /**
     * Default empty constructor.
     */
    public DataSetRow() {
    }

    /**
     * Constructor with values.
     * 
     * @param values the row value.
     */
    public DataSetRow(Map<String, String> values) {
        this.values.putAll(values);
    }

    /**
     * Set an entry in the dataset row
     * 
     * @param name - the key
     * @param value - the value
     */
    public DataSetRow set(final String name, final String value) {
        values.put(name, value);
        return this;
    }

    /**
     * Get the value associated with the provided key
     * 
     * @param name - the key
     * @return - the value as string
     */
    public String get(final String name) {
        return values.get(name);
    }

    /**
     * Check if the row is deleted
     */
    public boolean isDeleted() {
        return this.deleted;
    }

    /**
     * Set whether the row is deleted
     */
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    /**
     * Set the old row for diff
     * 
     * @param oldRow - the original row
     */
    public void diff(final DataSetRow oldRow) {
        this.oldRow = oldRow;
    }

    /**
     * Here we decide the flags to set and write is to the response
     * <ul>
     * <li>flag NEW : deleted by old but not by new</li>
     * <li>flag UPDATED : not deleted at all and value has changed</li>
     * <li>flag DELETED : not deleted by old by is by new</li>
     * </ul>
     */
    public Map<String, Object> values() {
        final Map<String, Object> result = new HashMap<>(values.size() + 1);
        if (this.oldRow == null) {
            result.putAll(values);
        } else {
            // row is no more deleted : we write row values with the *NEW* flag
            if (oldRow.isDeleted() && !isDeleted()) {
                result.put(ROW_DIFF_KEY, ROW_DIFF_NEW);
                result.putAll(values);
            }

            // row has been deleted : we write row values with the *DELETED* flag
            else if (!oldRow.isDeleted() && isDeleted()) {
                result.put(ROW_DIFF_KEY, ROW_DIFF_DELETED);
                result.putAll(oldRow.values());
            }

            // row has been updated : write the new values and get the diff for each value, then write the DIFF_KEY
            // property
            else {
                final Map<String, Object> diff = new HashMap<>();
                final Map<String, Object> originalValues = oldRow.values();

                values.entrySet().stream().forEach((entry) -> {
                    final Object originalValue = originalValues.get(entry.getKey());
                    if (!StringUtils.equals(entry.getValue(), (String) originalValue)) {
                        diff.put(entry.getKey(), DIFF_UPDATE);
                    }
                });

                result.putAll(values);
                result.put(DIFF_KEY, diff);
            }
        }

        return result;
    }

    /**
     * Clear all values in this row and reset state as it was when created (e.g. {@link #isDeleted()} returns
     * <code>false</code>).
     */
    public void clear() {
        deleted = false;
        values.clear();
        oldRow = null;
    }

    /**
     * @see Cloneable#clone()
     */
    @Override
    public DataSetRow clone() {
        final DataSetRow clone = new DataSetRow(values);
        clone.setDeleted(this.isDeleted());
        return clone;
    }

    /**
     * Determine if the row should be written
     */
    public boolean shouldWrite() {
        if (this.oldRow == null) {
            return !isDeleted();
        } else {
            return !oldRow.isDeleted() || !isDeleted();
        }
    }

    /**
     * Rename the column.
     *
     * @param columnName the name of the column to rename.
     * @param newColumnName the new column name.
     */
    public void renameColumn(String columnName, String newColumnName) {

        // defensive programming against
        if (values.containsKey(columnName) == false) {
            return;
        }

        // columns cannot have the same name
        if (values.containsKey(newColumnName)) {
            throw new IllegalArgumentException("column '" + newColumnName + "' already exists");
        }

        synchronized (values) {
            String savedValue = values.get(columnName);
            values.remove(columnName);
            values.put(newColumnName, savedValue);
        }
    }

    /**
     * @see Objects#equals(Object, Object)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        DataSetRow that = (DataSetRow) o;
        return Objects.equals(deleted, that.deleted) && Objects.equals(values, that.values);
    }

    /**
     * @see Objects#hash(Object...)
     */
    @Override
    public int hashCode() {
        return Objects.hash(deleted, values);
    }

}
