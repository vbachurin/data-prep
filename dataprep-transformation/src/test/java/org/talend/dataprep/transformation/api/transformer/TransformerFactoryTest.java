package org.talend.dataprep.transformation.api.transformer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.talend.dataprep.transformation.format.CSVFormat.CSV;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.talend.dataprep.api.dataset.DataSet;
import org.talend.dataprep.transformation.Application;
import org.talend.dataprep.transformation.api.transformer.configuration.Configuration;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class TransformerFactoryTest {

    @Autowired
    Jackson2ObjectMapperBuilder builder;

    @Autowired
    private TransformerFactory factory;

    @Test
    public void getExporter_csv_exporter_should_write_csv_format() throws Exception {
        // given
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("exportParameters.csvSeparator", ";");
        final OutputStream outputStream = new ByteArrayOutputStream();
        final Configuration configuration = Configuration.builder() //
                .args(arguments) //
                .format(CSV) //
                .output(outputStream) //
                .actions(IOUtils.toString(TransformerFactoryTest.class.getResourceAsStream("upper_case_firstname.json"))) //
                .build();
        final Transformer transformer = factory.get(configuration);
        final String expectedCsv = IOUtils.toString(
                TransformerFactoryTest.class
                .getResourceAsStream("expected_export_preparation_uppercase_firstname.csv"));

        final ObjectMapper mapper = builder.build();
        final InputStream inputStream = TransformerFactoryTest.class.getResourceAsStream("../../format/export_dataset.json");
        try (JsonParser parser = mapper.getFactory().createParser(inputStream)) {
            final DataSet dataSet = mapper.readerFor(DataSet.class).readValue(parser);

            // when
            transformer.transform(dataSet, configuration);
            // then
            assertThat(outputStream.toString()).isEqualTo(expectedCsv);
        }

    }
}