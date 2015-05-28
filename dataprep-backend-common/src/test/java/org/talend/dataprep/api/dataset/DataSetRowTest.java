package org.talend.dataprep.api.dataset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.talend.dataprep.api.dataset.diff.Flag.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.talend.dataprep.api.dataset.diff.Flag;
import org.talend.dataprep.api.dataset.diff.FlagNames;

public class DataSetRowTest {

    /**
     * Test the new flag.
     */
    @Test
    public void diff_with_new_row_flag() {
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
    public void diff_with_delete_row_flag() {
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
    public void diff_with_update_record_flag() {
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
     * test the new flag on records.
     */
    @Test
    public void diff_with_new_flag() {
        DataSetRow row = createRow(defaultValues(), false);

        Map<String, String> oldValues = new HashMap<>(2);
        oldValues.put("id", "1");
        oldValues.put("age", "18");
        DataSetRow oldRow = createRow(oldValues, false);

        row.diff(oldRow);

        Map<String, Object> diff = (Map<String, Object>) row.values().get(FlagNames.DIFF_KEY);

        // firstName and lastName are new
        List<String> expected = new ArrayList<>(2);
        expected.add("firstName");
        expected.add("lastName");

        for (String expectedKey : expected) {
            assertTrue(diff.containsKey(expectedKey));
            assertEquals(Flag.NEW.getValue(), diff.get(expectedKey));
        }
    }

    /**
     * test the delete flag on records.
     */
    @Test
    public void diff_with_delete_flag() {
        DataSetRow oldRow = createRow(defaultValues(), false);

        final Map<String, String> values = new HashMap<>(4);
        values.put("id", "1");
        values.put("age", "18");
        DataSetRow row = createRow(values, false);

        row.diff(oldRow);

        Map<String, Object> diff = (Map<String, Object>) row.values().get(FlagNames.DIFF_KEY);

        // firstName and lastName are new
        List<String> expected = new ArrayList<>(2);
        expected.add("firstName");
        expected.add("lastName");

        for (String expectedKey : expected) {
            assertTrue(diff.containsKey(expectedKey));
            assertEquals(Flag.DELETE.getValue(), diff.get(expectedKey));
        }
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