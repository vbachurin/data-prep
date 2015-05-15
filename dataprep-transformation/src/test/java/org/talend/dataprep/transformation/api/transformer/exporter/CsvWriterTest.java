package org.talend.dataprep.transformation.api.transformer.exporter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.talend.dataprep.api.type.ExportType.CSV;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.transformation.Application;
import org.talend.dataprep.transformation.api.transformer.Transformer;
import org.talend.dataprep.transformation.api.transformer.exporter.csv.CsvExporter;
import org.talend.dataprep.transformation.api.transformer.exporter.csv.CsvWriter;

public class CsvWriterTest {

    private CsvWriter writer;

    private OutputStream outputStream;

    @Before
    public void init() {
        outputStream = new ByteArrayOutputStream();
        writer = new CsvWriter(outputStream, ';');
    }

    @Test
    public void write_should_write_columns() throws Exception {
        // given
        final ColumnMetadata column1 = new ColumnMetadata("id", "string");
        final ColumnMetadata column2 = new ColumnMetadata("firstname", "string");

        final List<ColumnMetadata> columns = new ArrayList<>(2);
        columns.add(column1);
        columns.add(column2);

        // when
        writer.write(columns);
        writer.flush();

        // then
        assertThat(outputStream.toString()).isEqualTo("\"id\";\"firstname\"\n");
    }

    @Test
    public void write_should_throw_exception_when_write_columns_have_not_been_called() throws Exception {
        // given
        final DataSetRow row = new DataSetRow();

        // when
        try {
            writer.write(row);
            fail("should have thrown UnsupportedOperationException");
        }
        //then
        catch (final UnsupportedOperationException e) {
            assertThat(e.getMessage()).isEqualTo("Write columns should be called before to init column list");
        }
    }

    @Test
    public void write_should_write_row() throws Exception {
        // given
        final ColumnMetadata column1 = new ColumnMetadata("id", "string");
        final ColumnMetadata column2 = new ColumnMetadata("firstname", "string");
        final List<ColumnMetadata> columns = new ArrayList<>(2);
        columns.add(column1);
        columns.add(column2);

        final DataSetRow row = new DataSetRow();
        row.set("id", "64a5456ac148b64524ef165");
        row.set("firstname", "Superman");

        final String expectedCsv = "\"id\";\"firstname\"\n" +
                "\"64a5456ac148b64524ef165\";\"Superman\"\n";

        // when
        writer.write(columns);
        writer.write(row);
        writer.flush();

        //then
        assertThat(outputStream.toString()).isEqualTo(expectedCsv);
    }

}