package org.talend.dataprep.transformation.api.transformer.exporter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.talend.dataprep.api.type.ExportType.CSV;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.talend.dataprep.transformation.Application;
import org.talend.dataprep.transformation.api.transformer.Transformer;
import org.talend.dataprep.transformation.api.transformer.exporter.csv.CsvExporter;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class ExportFactoryTest {

    @Autowired
    private ExportFactory factory;

    @Test
    public void getExporter_should_create_csv_exporter() throws Exception {
        // given
        HashMap<String, Object> arguments = new HashMap<>();
        arguments.put("csvSeparator", ';');
        final ExportConfiguration configuration = ExportConfiguration.builder().args(arguments).format(CSV)
                .actions(IOUtils.toString(ExportFactory.class.getResourceAsStream("upper_case_firstname.json"))).build();

        // when
        final Transformer exporter = factory.getExporter(configuration);

        // then
        assertThat(exporter).isInstanceOf(CsvExporter.class);
    }

    @Test
    public void getExporter_csv_exporter_should_write_csv_format() throws Exception {
        // given
        HashMap<String, Object> arguments = new HashMap<>();
        arguments.put("csvSeparator", ';');
        final ExportConfiguration configuration = ExportConfiguration.builder().args(arguments).format(CSV)
                .actions(IOUtils.toString(ExportFactory.class.getResourceAsStream("upper_case_firstname.json"))).build();
        final Transformer exporter = factory.getExporter(configuration);
        final String expectedCsv = IOUtils.toString(ExportFactory.class
                .getResourceAsStream("expected_export_preparation_uppercase_firstname.csv"));

        final InputStream inputStream = ExportFactory.class.getResourceAsStream("export_dataset.json");
        final OutputStream outputStream = new ByteArrayOutputStream();

        // when
        exporter.transform(inputStream, outputStream);

        // then
        assertThat(outputStream.toString()).isEqualTo(expectedCsv);
    }
}