package org.talend.dataprep.api.dataset;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

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
    @Test(expected = IllegalArgumentException.class)
    public void rename_to_an_existing_column() {
        // given
        String oldName = "firstName";
        String newName = "lastName";
        final DataSetRow row = createRow(defaultValues(), false);

        // when
        row.renameColumn(oldName, newName);

        // then
        fail("two columns cannot have the same name.");
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