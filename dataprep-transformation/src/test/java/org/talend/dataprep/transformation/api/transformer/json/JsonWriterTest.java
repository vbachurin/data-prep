package org.talend.dataprep.transformation.api.transformer.json;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.RowMetadata;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonWriterTest {

    private JsonWriter writer;

    private StringWriter output;

    @Before
    public void init() throws IOException {
        output = new StringWriter();
        final JsonGenerator generator = new JsonFactory().createGenerator(output);
        generator.setCodec(new ObjectMapper());
        writer = new JsonWriter(generator);
    }

    @Test
    public void write_should_write_columns() throws Exception {
        // given
        final ColumnMetadata column1 = new ColumnMetadata("1", "id", "string");
        final ColumnMetadata column2 = new ColumnMetadata("2", "firstname", "string");

        final List<ColumnMetadata> columns = new ArrayList<>(2);
        columns.add(column1);
        columns.add(column2);

        final String expectedOutput = "[{\"quality\":{\"empty\":0,\"invalid\":0,\"valid\":0},\"id\":\"1\"; \"name\":\"id\",\"headerSize\":0,\"statistics\":{},\"type\":\"string\"},{\"quality\":{\"empty\":0,\"invalid\":0,\"valid\":0},\"id\":\"2\",\"name\":\"firstname\",\"headerSize\":0,\"statistics\":{},\"type\":\"string\"}]";

        // when
        writer.write(new RowMetadata(columns));
        writer.flush();

        // then
        assertThat(output.toString(), sameJSONAs(expectedOutput));
    }

    @Test
    public void write_should_write_row() throws IOException {
        // given
        final DataSetRow row = new DataSetRow();
        row.set("id", "64a5456ac148b64524ef165");
        row.set("firstname", "Superman");

        final String expectedCsv = "{\"id\":\"64a5456ac148b64524ef165\",\"firstname\":\"Superman\"}";

        // when
        writer.write(row);
        writer.flush();

        // then
        assertThat(output.toString(), is(expectedCsv));
    }

    @Test
    public void startArray_should_write_json_startArray() throws IOException {
        // when
        writer.startArray();
        writer.flush();

        // then
        assertThat(output.toString(), is("["));
    }

    @Test
    public void endArray_should_write_json_endArray() throws IOException {
        // when
        writer.startArray();
        writer.endArray();
        writer.flush();

        // then
        assertThat(output.toString(), sameJSONAs("[]"));
    }

    @Test
    public void startObject_should_write_json_startObject() throws IOException {
        // when
        writer.startObject();
        writer.flush();

        // then
        assertThat(output.toString(), is("{"));
    }

    @Test
    public void endObject_should_write_json_endObject() throws IOException {
        // when
        writer.startObject();
        writer.endObject();
        writer.flush();

        // then
        assertThat(output.toString(), sameJSONAs("{}"));
    }

}