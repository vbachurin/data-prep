package org.talend.dataprep.schema;

import java.io.InputStream;
import java.util.List;

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

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = XlsFormatGuesserTest.class)
@Configuration
@ComponentScan(basePackages = "org.talend.dataprep")
@EnableAutoConfiguration
public class XlsFormatGuesserTest {

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
}
