package org.talend.dataprep.api.dataset;

import static org.talend.dataprep.api.dataset.diff.Flag.*;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.talend.dataprep.api.dataset.diff.FlagNames;

/**
 * A DataSetRow is a row of a dataset. Values in data set row are <b>alphabetically</b> ordered by name.
 */
public class DataSetRow implements Cloneable {

    /** True if this row is deleted. */
    private boolean deleted;

    /** the old value used for the diff. */
    private DataSetRow oldValue;

    /** Values of the dataset row. */
    private SortedMap<String, String> values;

    /**
     * Default empty constructor.
     */
    public DataSetRow() {
        values = new TreeMap<>();
        deleted = false;
    }

    /**
     * Constructor with values.
     * 
     * @param values the row value.
     */
    public DataSetRow(Map<String, String> values) {
        this();
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
     * @param id the column id.
     * @return - the value as string
     */
    public String get(final String id) {
        return values.get(id);
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
        this.oldValue = oldRow;
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

        final Map<String, Object> result = new LinkedHashMap<>(values.size() + 1);

        // if not old value, no diff to compute
        if (this.oldValue == null) {
            result.putAll(values);
            return result;
        }

        // row is no more deleted : we write row values with the *NEW* flag
        if (oldValue.isDeleted() && !isDeleted()) {
            result.put(FlagNames.ROW_DIFF_KEY, NEW.getValue());
            result.putAll(values);
        }
        // row has been deleted : we write row values with the *DELETED* flag
        else if (!oldValue.isDeleted() && isDeleted()) {
            result.put(FlagNames.ROW_DIFF_KEY, DELETE.getValue());
            result.putAll(oldValue.values());
        }

        // row has been updated : write the new values and get the diff for each value, then write the DIFF_KEY
        // property

        final Map<String, Object> diff = new HashMap<>();
        final Map<String, Object> originalValues = oldValue.values();

        // compute the new value (column is not found in old value)
        values.entrySet().stream().forEach(entry -> {
            if (!originalValues.containsKey(entry.getKey())) {
                diff.put(entry.getKey(), NEW.getValue());
            }
        });

        // compute the deleted values (column is deleted)
        originalValues.entrySet().stream().forEach((entry) -> {
            if (!values.containsKey(entry.getKey())) {
                diff.put(entry.getKey(), DELETE.getValue());
                // put back the original entry so that the value can be displayed
                values.put(entry.getKey(), (String) entry.getValue());
            }
        });

        // compute the update values (column is still here but value is different)
        values.entrySet().stream().forEach((entry) -> {
            if (originalValues.containsKey(entry.getKey())) {
                final Object originalValue = originalValues.get(entry.getKey());
                if (!StringUtils.equals(entry.getValue(), (String) originalValue)) {
                    diff.put(entry.getKey(), UPDATE.getValue());
                }
            }
        });

        result.putAll(values);
        if (!diff.isEmpty()) {
            result.put(FlagNames.DIFF_KEY, diff);
        }

        return result;
    }

    /**
     * Clear all values in this row and reset state as it was when created (e.g. {@link #isDeleted()} returns
     * <code>false</code>).
     */
    public void clear() {
        deleted = false;
        oldValue = null;
        values.clear();
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
        if (this.oldValue == null) {
            return !isDeleted();
        } else {
            return !oldValue.isDeleted() || !isDeleted();
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

    @Override
    public String toString() {
        return "DataSetRow{" + "deleted=" + deleted + ", oldValue=" + oldValue + ", values=" + values + '}';
    }

    /**
     * Order values of this data set row according to <code>columns</code>. This method clones the current record, so
     * no need to call {@link #clone()}.
     * @param columns The columns to be used to order values.
     * @return A new data set row for method with values ordered following <code>columns</code>.
     */
    public DataSetRow order(List<ColumnMetadata> columns) {
        if (columns == null) {
            throw new IllegalArgumentException("Columns cannot be null.");
        }
        if (columns.isEmpty()) {
            return clone();
        }
        if (columns.size() != values.size()) {
            throw new IllegalArgumentException("Expected " + values.size() + " columns but got " + columns.size());
        }

        List<String> idIndexes = columns.stream().map(ColumnMetadata::getId).collect(Collectors.toList());
        SortedMap<String, String> orderedValues = new TreeMap<>((id1, id2) -> idIndexes.indexOf(id1) - idIndexes.indexOf(id2));
        orderedValues.putAll(values);
        final DataSetRow dataSetRow = new DataSetRow();
        dataSetRow.values = orderedValues;
        return dataSetRow;
    }
}
