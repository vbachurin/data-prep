package org.talend.dataprep.api.dataset;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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

    private DataSetRow createRow(final Map<String, String> values, final boolean isDeleted) {
        final DataSetRow row = new DataSetRow(values);
        row.setDeleted(isDeleted);

        return row;
    }

    private Map<String, String> defaultValues() {
        final Map<String, String> values = new HashMap<>(4);
        values.put("id", "1");
        values.put("firstName", "Toto");
        values.put("lastName", "Tata");
        values.put("age", "18");

        return values;
    }
}