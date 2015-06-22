package org.talend.dataprep.transformation.api.transformer.exporter;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.talend.dataprep.api.dataset.DataSet;
import org.talend.dataprep.api.type.ExportType;
import org.talend.dataprep.schema.io.XlsUtils;
import org.talend.dataprep.transformation.Application;
import org.talend.dataprep.transformation.api.transformer.Transformer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@DirtiesContext
public class XlsExporterTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private Jackson2ObjectMapperBuilder builder;

    @Autowired
    private ExportFactory factory;

    @Test
    public void write_simple_xls_file() throws Exception {

        // given
        final ExportConfiguration configuration = ExportConfiguration.builder().format(ExportType.XLS).actions("").build();
        final Transformer exporter = factory.getExporter(configuration);

        final InputStream inputStream = ExportFactory.class.getResourceAsStream("export_dataset.json");

        Path path = Files.createTempFile("datarep-foo", "xls");

        Files.deleteIfExists(path);
        final ObjectMapper mapper = builder.build();
        try (JsonParser parser = mapper.getFactory().createParser(inputStream)) {
            final DataSet dataSet = mapper.reader(DataSet.class).readValue(parser);
            try (final OutputStream outputStream = Files.newOutputStream(path)) {
                // when
                exporter.transform(dataSet, outputStream);
                outputStream.flush();
            }
        }

        Workbook workbook = XlsUtils.getWorkbook(Files.newInputStream(path));

        Assertions.assertThat(workbook).isNotNull();

        Assertions.assertThat(workbook.getNumberOfSheets()).isEqualTo(1);

        Sheet sheet = workbook.getSheetAt(0);

        Assertions.assertThat(sheet).isNotNull().isNotEmpty();

        Assertions.assertThat(sheet.getFirstRowNum()).isEqualTo(0);

        Assertions.assertThat(sheet.getLastRowNum()).isEqualTo(6);

        // assert header content
        Row row = sheet.getRow(0);
        /*
         * "columns": [ { "id": "id", "type": "string" }, { "id": "firstname", "type": "string" }, { "id": "lastname",
         * "type": "string" }, { "id": "age", "type": "integer" }, { "id": "date-of-birth", "type": "date" }, { "id":
         * "alive", "type": "boolean" }, { "id": "city", "type": "string" } ]
         */
        Assertions.assertThat(row.getCell(0).getRichStringCellValue().getString()).isEqualTo("id");
        Assertions.assertThat(row.getCell(1).getRichStringCellValue().getString()).isEqualTo("firstname");
        Assertions.assertThat(row.getCell(2).getRichStringCellValue().getString()).isEqualTo("lastname");
        Assertions.assertThat(row.getCell(3).getRichStringCellValue().getString()).isEqualTo("age");
        Assertions.assertThat(row.getCell(4).getRichStringCellValue().getString()).isEqualTo("date-of-birth");
        Assertions.assertThat(row.getCell(5).getRichStringCellValue().getString()).isEqualTo("alive");
        Assertions.assertThat(row.getCell(6).getRichStringCellValue().getString()).isEqualTo("city");

        // assert first content
        row = sheet.getRow(1);
        /*
         * { "id" : "1", "firstname" : "Clark", "lastname" : "Kent", "age" : "42", "date-of-birth" : "10/09/1940",
         * "alive" : "false", "city" : "Smallville" }
         */

        Assertions.assertThat(row.getCell(0).getStringCellValue()).isEqualTo("1");
        Assertions.assertThat(row.getCell(1).getStringCellValue()).isEqualTo("Clark");
        Assertions.assertThat(row.getCell(2).getStringCellValue()).isEqualTo("Kent");
        Assertions.assertThat(row.getCell(3).getNumericCellValue()).isEqualTo((double) 42);
        Assertions.assertThat(row.getCell(4).getStringCellValue()).isEqualTo("10/09/1940");
        Assertions.assertThat(row.getCell(5).getBooleanCellValue()).isFalse();
        Assertions.assertThat(row.getCell(6).getStringCellValue()).isEqualTo("Smallville");

        // assert last content
        row = sheet.getRow(sheet.getLastRowNum());
        /*
         * { "id" : "6", "firstname" : "Ray", "lastname" : "Palmer", "age" : "93", "date-of-birth" : "01/05/1951",
         * "alive" : "true", "city" : "Star city" }
         */
        Assertions.assertThat(row.getCell(0).getStringCellValue()).isEqualTo("6");
        Assertions.assertThat(row.getCell(1).getStringCellValue()).isEqualTo("Ray");
        Assertions.assertThat(row.getCell(2).getStringCellValue()).isEqualTo("Palmer");
        Assertions.assertThat(row.getCell(3).getNumericCellValue()).isEqualTo((double) 93);
        Assertions.assertThat(row.getCell(4).getStringCellValue()).isEqualTo("01/05/1951");
        Assertions.assertThat(row.getCell(5).getBooleanCellValue()).isTrue();
        Assertions.assertThat(row.getCell(6).getStringCellValue()).isEqualTo("Star city");
    }

}
