package org.talend.dataprep.api.dataset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.talend.dataprep.api.dataset.diff.Flag.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.talend.dataprep.api.dataset.diff.FlagNames;

public class DataSetRowTest {

    /**
     * Test the rename column method.
     *
     * @see DataSetRow#renameColumn(String, String)
     */
    @Test
    public void rename_should_work() {
        // given
        String oldName = "firstName";
        String newName = "NAME_FIRST";
        final DataSetRow row = createRow(defaultValues(), false);
        String expected = row.get(oldName);

        // when
        row.renameColumn(oldName, newName);

        // then
        assertNull(row.get(oldName));
        assertEquals(expected, row.get(newName));
    }

    /**
     * Test the rename column method on a non existing column.
     *
     * @see DataSetRow#renameColumn(String, String)
     */
    @Test
    public void rename_on_non_existing_column_should_not_throw_error() {
        // given
        final DataSetRow row = createRow(defaultValues(), false);

        // when
        row.renameColumn("non existing column", "new name");

        // then
        assertEquals(row, createRow(defaultValues(), false));
    }

    /**
     * When rename overwrite an existing column.
     *
     * @see DataSetRow#renameColumn(String, String)
     */
    @Test
    public void rename_to_an_existing_column() {
        // given
        String oldName = "firstName";
        String newName = "lastName";
        final DataSetRow row = createRow(defaultValues(), false);

        // when
        row.renameColumn(oldName, newName);

        // then
        assertEquals(row, createRow(defaultValues(), false));
    }

    /**
     * Test the new flag.
     */
    @Test
    public void diff_with_new_flag() {
        DataSetRow row = createRow(defaultValues(), false);
        DataSetRow oldRow = createRow(defaultValues(), true);

        row.diff(oldRow);
        Map<String, Object> actual = row.values();
        assertEquals(actual.get(FlagNames.ROW_DIFF_KEY), NEW.getValue());
    }

    /**
     * Test the delete flag.
     */
    @Test
    public void diff_with_delete_flag() {
        DataSetRow row = createRow(defaultValues(), true);
        DataSetRow oldRow = createRow(defaultValues(), false);

        row.diff(oldRow);
        Map<String, Object> actual = row.values();
        assertEquals(actual.get(FlagNames.ROW_DIFF_KEY), DELETE.getValue());
    }

    /**
     * test the update flag.
     */
    @Test
    public void diff_with_update_flag() {
        DataSetRow row = createRow(defaultValues(), false);

        final Map<String, String> oldValues = new HashMap<>(4);
        oldValues.put("id", "2");
        oldValues.put("firstName", "otoT");
        oldValues.put("lastName", "ataT");
        oldValues.put("age", "81");
        DataSetRow oldRow = createRow(oldValues, false);

        row.diff(oldRow);
        Map<String, Object> actual = row.values();
        Map<String, Object> diff = (Map<String, Object>) actual.get(FlagNames.DIFF_KEY);
        diff.values().forEach(value -> assertEquals(value, UPDATE.getValue()));
    }

    /**
     * @param values the values of the row to return.
     * @param isDeleted true if the row is deleted.
     * @return a new dataset row with the given values.
     */
    private DataSetRow createRow(final Map<String, String> values, final boolean isDeleted) {
        final DataSetRow row = new DataSetRow(values);
        row.setDeleted(isDeleted);

        return row;
    }

    /**
     * @return some default values.
     */
    private Map<String, String> defaultValues() {
        final Map<String, String> values = new HashMap<>(4);
        values.put("id", "1");
        values.put("firstName", "Toto");
        values.put("lastName", "Tata");
        values.put("age", "18");

        return values;
    }
}