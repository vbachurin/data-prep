package org.talend.dataprep.api.dataset;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DataSetRowTest {

    private StringWriter writer;
    private JsonGenerator jsonGenerator;

    @Before
    public void init() throws IOException {
        writer = new StringWriter();
        jsonGenerator = getGenerator(writer);
    }

    @Test
    public void writePreviewTo_should_not_write_if_row_stay_deleted() throws Exception {
        //given
        final DataSetRow previewRow = createRow(defaultValues(), true);
        final DataSetRow oldRow = createRow(defaultValues(), true);

        //when
        previewRow.writePreviewTo(jsonGenerator, oldRow);

        //then
        assertThat(writer.toString()).isEqualTo("");
    }

    @Test
    public void writePreviewTo_should_write_with_NEW_flag_if_row_is_no_more_deleted() throws Exception {
        //given
        final DataSetRow previewRow = createRow(defaultValues(), false);
        final DataSetRow oldRow = createRow(defaultValues(), true);

        //when
        previewRow.writePreviewTo(jsonGenerator, oldRow);

        //then
        assertThat(writer.toString()).isEqualTo("{\"__tdpRowDiff\":\"new\",\"firstName\":\"Toto\",\"lastName\":\"Tata\",\"id\":\"1\",\"age\":\"18\"}");
    }

    @Test
    public void writePreviewTo_should_write_with_DELETED_flag_if_row_has_been_deleted() throws Exception {
        //given
        final DataSetRow previewRow = createRow(defaultValues(), true);
        final DataSetRow oldRow = createRow(defaultValues(), false);

        //when
        previewRow.writePreviewTo(jsonGenerator, oldRow);

        //then
        assertThat(writer.toString()).isEqualTo("{\"__tdpRowDiff\":\"delete\",\"firstName\":\"Toto\",\"lastName\":\"Tata\",\"id\":\"1\",\"age\":\"18\"}");
    }

    @Test
    public void writePreviewTo_should_write_with_diff_details_if_row_has_been_modified() throws Exception {
        //given
        final Map<String, String> previewValues = defaultValues();
        previewValues.put("firstName", "New Toto");
        previewValues.put("lastName", "New Tata");
        final DataSetRow previewRow = createRow(previewValues, false);
        final DataSetRow oldRow = createRow(defaultValues(), false);

        //when
        previewRow.writePreviewTo(jsonGenerator, oldRow);

        //then
        assertThat(writer.toString()).isEqualTo("{\"firstName\":\"New Toto\",\"lastName\":\"New Tata\",\"id\":\"1\",\"age\":\"18\",\"__tdpDiff\":{\"firstName\":\"update\",\"lastName\":\"update\"}}");
    }

    private JsonGenerator getGenerator(final Writer writer) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        return mapper.getFactory().createGenerator(writer);
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