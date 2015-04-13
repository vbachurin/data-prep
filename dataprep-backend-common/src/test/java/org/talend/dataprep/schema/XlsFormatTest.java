package org.talend.dataprep.schema;

import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.assertj.core.api.Assertions;
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

import com.fasterxml.jackson.databind.ObjectMapper;

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
        logger.info("class for bean with id {} is {}", beanId, formatGuesser.getClass());
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

        FormatGuess formatGuess;

        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("test.xls")) {
            FormatGuesser formatGuesser = applicationContext.getBean(beanId, FormatGuesser.class);
            formatGuess = formatGuesser.guess(inputStream);
            Assert.assertNotNull(formatGuess);
            Assert.assertTrue(formatGuess instanceof XlsFormatGuess);
            Assert.assertEquals(XlsFormatGuess.MEDIA_TYPE, formatGuess.getMediaType());
        }

        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("test.xls")) {
            List<ColumnMetadata> columnMetadatas = formatGuess.getSchemaParser().parse(inputStream);
            Assertions.assertThat(columnMetadatas).isNotNull().isNotEmpty().hasSize(4);
        }

    }

    @Test
    public void read_xls_file_then_serialize() throws Exception {

        FormatGuess formatGuess;

        DataSetMetadata dataSetMetadata = DataSetMetadata.Builder.metadata().id("beer").build();

        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("test.xls")) {
            FormatGuesser formatGuesser = applicationContext.getBean(beanId, FormatGuesser.class);

            formatGuess = formatGuesser.guess(inputStream);
        }

        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("test.xls")) {

            List<ColumnMetadata> columnMetadatas = formatGuess.getSchemaParser().parse(inputStream);

            dataSetMetadata.getRow().setColumns(columnMetadatas);

        }

        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("test.xls")) {

            Serializer serializer = applicationContext.getBean("serializer#xls", Serializer.class);

            InputStream jsonStream = serializer.serialize(inputStream, dataSetMetadata);

            String json = IOUtils.toString(jsonStream);

            logger.info("json: {}", json);

            // {"col0":"beer name ","col1":"country","col2":"quality","col3":"note"},
            // {"col0":"Little Creatures","col1":"Australie","col2":"Awesome","col3":"10.0"}
            // {"col0":"Heinekein","col1":"France ","col2":"crappy","col3":""}
            // {"col0":"Foo","col1":"Australie","col2":"10.0","col3":"6.0"}
            // {"col0":"Bar","col1":"France ","col2":"crappy","col3":"2.0"}

            ObjectMapper mapper = new ObjectMapper();

            List values = mapper.readValue(json, List.class);

            logger.info("values: {}", values);

            Assertions.assertThat(values).isNotEmpty().hasSize(4)
                    .containsExactly(new TestObject("Little Creatures", "Australie", "Awesome", "10.0"), //
                            new TestObject("Heinekein", "France ", "crappy", ""), //
                            new TestObject("Foo", "Australie", "10.0", "6.0"), //
                            new TestObject("Bar", "France ", "crappy", "2.0"));

        }

    }

    static class TestObject {

        private String col0, col1, col2, col3;

        public TestObject() {
        }

        public TestObject(String col0, String col1, String col2, String col3) {
            this.col0 = col0;
            this.col1 = col1;
            this.col2 = col2;
            this.col3 = col3;
        }

        public String getCol0() {
            return col0;
        }

        public void setCol0(String col0) {
            this.col0 = col0;
        }

        public String getCol1() {
            return col1;
        }

        public void setCol1(String col1) {
            this.col1 = col1;
        }

        public String getCol2() {
            return col2;
        }

        public void setCol2(String col2) {
            this.col2 = col2;
        }

        public String getCol3() {
            return col3;
        }

        public void setCol3(String col3) {
            this.col3 = col3;
        }
    }
}
