package org.talend.dataprep.api.dataset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Unit test for DataSetRow.
 * 
 * @see DataSetRow
 */
public class DataSetRowTest {

    /** The json writer. */
    private StringWriter writer;

    /** The json generator. */
    private JsonGenerator jsonGenerator;

    /**
     * Initialization before each test.
     * 
     * @throws IOException if an error occurs.
     */
    @Before
    public void init() throws IOException {
        writer = new StringWriter();
        jsonGenerator = getGenerator(writer);
    }

    @Test
    public void writePreviewTo_should_not_write_if_row_stay_deleted() throws Exception {
        // given
        final DataSetRow previewRow = createRow(defaultValues(), true);
        final DataSetRow oldRow = createRow(defaultValues(), true);

        // when
        previewRow.writePreviewTo(jsonGenerator, oldRow);

        // then
        assertThat(writer.toString()).isEqualTo("");
    }

    @Test
    public void writePreviewTo_should_write_with_NEW_flag_if_row_is_no_more_deleted() throws Exception {
        // given
        final DataSetRow previewRow = createRow(defaultValues(), false);
        final DataSetRow oldRow = createRow(defaultValues(), true);

        // when
        previewRow.writePreviewTo(jsonGenerator, oldRow);

        // then
        assertThat(writer.toString()).isEqualTo(
                "{\"__tdpRowDiff\":\"new\",\"firstName\":\"Toto\",\"lastName\":\"Tata\",\"id\":\"1\",\"age\":\"18\"}");
    }

    @Test
    public void writePreviewTo_should_write_with_DELETED_flag_if_row_has_been_deleted() throws Exception {
        // given
        final DataSetRow previewRow = createRow(defaultValues(), true);
        final DataSetRow oldRow = createRow(defaultValues(), false);

        // when
        previewRow.writePreviewTo(jsonGenerator, oldRow);

        // then
        assertThat(writer.toString()).isEqualTo(
                "{\"__tdpRowDiff\":\"delete\",\"firstName\":\"Toto\",\"lastName\":\"Tata\",\"id\":\"1\",\"age\":\"18\"}");
    }

    @Test
    public void writePreviewTo_should_write_with_diff_details_if_row_has_been_modified() throws Exception {
        // given
        final Map<String, String> previewValues = defaultValues();
        previewValues.put("firstName", "New Toto");
        previewValues.put("lastName", "New Tata");
        final DataSetRow previewRow = createRow(previewValues, false);
        final DataSetRow oldRow = createRow(defaultValues(), false);

        // when
        previewRow.writePreviewTo(jsonGenerator, oldRow);

        // then
        assertThat(writer.toString())
                .isEqualTo(
                        "{\"firstName\":\"New Toto\",\"lastName\":\"New Tata\",\"id\":\"1\",\"age\":\"18\",\"__tdpDiff\":{\"firstName\":\"update\",\"lastName\":\"update\"}}");
    }

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
     * @param writer where the generator should write.
     * @return a json generator that writes in the given writer.
     * @throws IOException if an error occurs.
     */
    private JsonGenerator getGenerator(final Writer writer) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        return mapper.getFactory().createGenerator(writer);
    }

    /**
     * @param values the values to use.
     * @param isDeleted true if this row should be deleted.
     * @return a new row from the parameters.
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