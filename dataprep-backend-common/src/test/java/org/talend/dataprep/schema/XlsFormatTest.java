package org.talend.dataprep.schema;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.MapEntry;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.api.type.Type;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = XlsFormatTest.class)
@Configuration
@ComponentScan(basePackages = "org.talend.dataprep")
@EnableAutoConfiguration
public class XlsFormatTest {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    ApplicationContext applicationContext;

    String beanId = "formatGuesser#xls";

    @Test
    public void ensure_xls_format_guesser_component_exists() {

        FormatGuesser formatGuesser = applicationContext.getBean(beanId, FormatGuesser.class);
        Assert.assertNotNull(formatGuesser);
        Assert.assertTrue(formatGuesser instanceof XlsFormatGuesser);
        logger.debug("class for bean with id {} is {}", beanId, formatGuesser.getClass());
    }

    @Test
    public void read_bad_xls_file() throws Exception {
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("fake.xls")) {
            FormatGuesser formatGuesser = applicationContext.getBean(beanId, FormatGuesser.class);
            FormatGuess formatGuess = formatGuesser.guess(inputStream);
            Assert.assertNotNull(formatGuess);
            Assert.assertTrue(formatGuess instanceof NoOpFormatGuess);
        }
    }

    @Test
    public void read_xls_file() throws Exception {

        String fileName = "test.xls";

        FormatGuess formatGuess;

        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName)) {
            FormatGuesser formatGuesser = applicationContext.getBean(beanId, FormatGuesser.class);
            formatGuess = formatGuesser.guess(inputStream);
            Assert.assertNotNull(formatGuess);
            Assert.assertTrue(formatGuess instanceof XlsFormatGuess);
            Assert.assertEquals(XlsFormatGuess.MEDIA_TYPE, formatGuess.getMediaType());
        }

        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName)) {
            List<ColumnMetadata> columnMetadatas = formatGuess.getSchemaParser().parse(inputStream);
            logger.debug("columnMetadatas: {}", columnMetadatas);
            Assertions.assertThat(columnMetadatas).isNotNull().isNotEmpty().hasSize(4);

            ColumnMetadata columnMetadataFound = columnMetadatas.stream()
                    .filter(columnMetadata -> StringUtils.equals(columnMetadata.getId(), "country")).findFirst().get();

            logger.debug("columnMetadataFound: {}", columnMetadataFound);

            Assertions.assertThat(columnMetadataFound.getType()).isEqualTo(Type.STRING.getName());

            columnMetadataFound = columnMetadatas.stream()
                    .filter(columnMetadata -> StringUtils.equals(columnMetadata.getId(), "note")).findFirst().get();

            logger.debug("columnMetadataFound: {}", columnMetadataFound);

            Assertions.assertThat(columnMetadataFound.getType()).isEqualTo(Type.NUMERIC.getName());

        }

    }

    @Test
    public void read_xls_file_then_serialize() throws Exception {

        String fileName = "test.xls";

        FormatGuess formatGuess;

        DataSetMetadata dataSetMetadata = DataSetMetadata.Builder.metadata().id("beer").build();

        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName)) {
            FormatGuesser formatGuesser = applicationContext.getBean(beanId, FormatGuesser.class);

            formatGuess = formatGuesser.guess(inputStream);
        }

        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName)) {

            List<ColumnMetadata> columnMetadatas = formatGuess.getSchemaParser().parse(inputStream);

            dataSetMetadata.getRow().setColumns(columnMetadatas);

        }

        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName)) {

            Serializer serializer = applicationContext.getBean("serializer#xls", Serializer.class);

            InputStream jsonStream = serializer.serialize(inputStream, dataSetMetadata);

            String json = IOUtils.toString(jsonStream);

            logger.debug("json: {}", json);

            ObjectMapper mapper = new ObjectMapper();

            CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, HashMap.class);

            List<Map<String, String>> values = mapper.readValue(json, collectionType);

            logger.debug("values: {}", values);

            // expected
            // {country=Australie, note=10.0, beer name =Little Creatures, quality=Awesome}
            // {country=France , note=, beer name =Heinekein, quality=crappy}
            // {country=Australie, note=6.0, beer name =Foo, quality=10.0}
            // {country=France , note=2.0, beer name =Bar, quality=crappy}

            Assertions.assertThat(values).isNotEmpty().hasSize(4);

            Assertions.assertThat(values.get(0)) //
                    .contains(MapEntry.entry("country", "Australie"),//
                            MapEntry.entry("note", "10.0"), //
                            MapEntry.entry("beer name", "Little Creatures"), //
                            MapEntry.entry("quality", "Awesome"));

            Assertions.assertThat(values.get(1)) //
                    .contains(MapEntry.entry("country", "France"),//
                            MapEntry.entry("note", ""), //
                            MapEntry.entry("beer name", "Heinekein"), //
                            MapEntry.entry("quality", "crappy"));

            Assertions.assertThat(values.get(2)) //
                    .contains(MapEntry.entry("country", "Australie"),//
                            MapEntry.entry("note", "6.0"), //
                            MapEntry.entry("beer name", "Foo"), //
                            MapEntry.entry("quality", "10.0"));

            Assertions.assertThat(values.get(3)) //
                    .contains(MapEntry.entry("country", "France"),//
                            MapEntry.entry("note", "2.0"), //
                            MapEntry.entry("beer name", "Bar"), //
                            MapEntry.entry("quality", "crappy"));

        }

    }

    @Test
    public void read_xls_cinema_then_serialize() throws Exception {

        String fileName = "EXPLOITATION-ListeEtabActifs_Adresse2012.xlsx";

        FormatGuess formatGuess;

        DataSetMetadata dataSetMetadata = DataSetMetadata.Builder.metadata().id("beer").build();

        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName)) {
            FormatGuesser formatGuesser = applicationContext.getBean(beanId, FormatGuesser.class);
            formatGuess = formatGuesser.guess(inputStream);
            Assert.assertNotNull(formatGuess);
            Assert.assertTrue(formatGuess instanceof XlsFormatGuess);
            Assert.assertEquals(XlsFormatGuess.MEDIA_TYPE, formatGuess.getMediaType());
        }

        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName)) {
            List<ColumnMetadata> columnMetadatas = formatGuess.getSchemaParser().parse(inputStream);

            dataSetMetadata.getRow().setColumns(columnMetadatas);

            logger.debug("columnMetadatas: {}", columnMetadatas);
            Assertions.assertThat(columnMetadatas).isNotNull().isNotEmpty().hasSize(8);

            ColumnMetadata columnMetadata = columnMetadatas.get(2);

            Assertions.assertThat(columnMetadata.getType()).isEqualTo(Type.NUMERIC.getName());

            Assertions.assertThat(columnMetadata.getHeaderSize()).isEqualTo(1);

            Assertions.assertThat(columnMetadata.getId()).isEqualTo("NoAuto");

        }

        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName)) {

            Serializer serializer = applicationContext.getBean("serializer#xls", Serializer.class);

            InputStream jsonStream = serializer.serialize(inputStream, dataSetMetadata);

            String json = IOUtils.toString(jsonStream);

            logger.debug("json: {}", json);

            ObjectMapper mapper = new ObjectMapper();

            CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, HashMap.class);

            List<Map<String, String>> values = mapper.readValue(json, collectionType);

            logger.debug("values: {}", values);

        }

    }

    @Test
    public void read_xls_musee_then_serialize() throws Exception {

        String fileName = "liste-musees-de-france-2012.xls";

        FormatGuess formatGuess;

        DataSetMetadata dataSetMetadata = DataSetMetadata.Builder.metadata().id("beer").build();

        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName)) {
            FormatGuesser formatGuesser = applicationContext.getBean(beanId, FormatGuesser.class);
            formatGuess = formatGuesser.guess(inputStream);
            Assert.assertNotNull(formatGuess);
            Assert.assertTrue(formatGuess instanceof XlsFormatGuess);
            Assert.assertEquals(XlsFormatGuess.MEDIA_TYPE, formatGuess.getMediaType());
        }

        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName)) {
            List<ColumnMetadata> columnMetadatas = formatGuess.getSchemaParser().parse(inputStream);
            logger.debug("columnMetadatas: {}", columnMetadatas);
            Assertions.assertThat(columnMetadatas).isNotNull().isNotEmpty().hasSize(13);

            ColumnMetadata columnMetadata = columnMetadatas.get(7);

            Assertions.assertThat(columnMetadata.getHeaderSize()).isEqualTo(1);

            Assertions.assertThat(columnMetadata.getId()).isEqualTo("CP");

            Assertions.assertThat(columnMetadata.getType()).isEqualTo(Type.STRING.getName());

        }

        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName)) {

            Serializer serializer = applicationContext.getBean("serializer#xls", Serializer.class);

            InputStream jsonStream = serializer.serialize(inputStream, dataSetMetadata);

            String json = IOUtils.toString(jsonStream);

            logger.debug("json: {}", json);

            ObjectMapper mapper = new ObjectMapper();

            CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, HashMap.class);

            List<Map<String, String>> values = mapper.readValue(json, collectionType);

            logger.debug("values: {}", values);

        }

    }

    @Test
    public void test_second_sheet_parsing() throws Exception {
        String fileName = "Talend_Desk-Tableau-Bord-011214.xls";

        FormatGuess formatGuess;

        XlsSchemaParser xlsSchemaParser = applicationContext.getBean(XlsSchemaParser.class);

        DataSetMetadata dataSetMetadata = DataSetMetadata.Builder.metadata().id("beer").build();

        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName)) {
            FormatGuesser formatGuesser = applicationContext.getBean(beanId, FormatGuesser.class);
            formatGuess = formatGuesser.guess(inputStream);
            Assert.assertNotNull(formatGuess);
            Assert.assertTrue(formatGuess instanceof XlsFormatGuess);
            Assert.assertEquals(XlsFormatGuess.MEDIA_TYPE, formatGuess.getMediaType());
        }

        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName)) {

            Map<String,List<ColumnMetadata>> xlsSchema = xlsSchemaParser.parseAllSheets( inputStream );

            List<ColumnMetadata> columnMetadatas = xlsSchema.values().iterator().next();
            logger.debug("columnMetadatas: {}", columnMetadatas);
            Assertions.assertThat(columnMetadatas).isNotNull().isNotEmpty().hasSize(14);

            ColumnMetadata columnMetadata = columnMetadatas.get(7);

            Assertions.assertThat(columnMetadata.getHeaderSize()).isEqualTo(1);

            Assertions.assertThat(columnMetadata.getId()).isEqualTo("telephone");

            Assertions.assertThat(columnMetadata.getType()).isEqualTo(Type.NUMERIC.getName());

        }

    }

}
