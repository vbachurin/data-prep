package org.talend.dataprep.schema;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Unit test for the LineBasedFormatGuesser.
 * 
 * @see LineBasedFormatGuesser
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = LineBasedFormatGuesserTest.class)
@Configuration
@ComponentScan(basePackages = "org.talend.dataprep")
@EnableAutoConfiguration
public class LineBasedFormatGuesserTest {


    /** The format guesser to test. */
    @Autowired
    LineBasedFormatGuesser guesser;

    /**
     * Standard csv file.
     */
    @Test
    public void shouldGuessCSV() throws IOException {

        String fileName = "org/talend/dataprep/schema/standard.csv";
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName)) {
            FormatGuesser.Result actual = guesser.guess(inputStream);

            Assert.assertNotNull(actual);
            Assert.assertTrue(actual.getFormatGuess() instanceof CSVFormatGuess);
        }
    }

    /**
     * csv file with 2 possible separators : ';' or '/', ';' should be selected
     */
    @Test
    public void shouldGuessBestSeparator() throws IOException {
        String fileName = "org/talend/dataprep/schema/mixed_separators.csv";
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName)) {
            FormatGuesser.Result actual = guesser.guess(inputStream);

            Assert.assertNotNull(actual);
            Assert.assertTrue(actual.getFormatGuess() instanceof CSVFormatGuess);
            char separator = actual.getParameters().get(CSVFormatGuess.SEPARATOR_PARAMETER).charAt(0);
            Assert.assertEquals(separator, ';');
        }
    }

    /**
     * Have a look at https://jira.talendforge.org/browse/TDP-181
     */
    @Test
    public void shouldGuessBestSeparatorOutOfTwo() throws IOException {
        String fileName = "org/talend/dataprep/schema/tdp-181.csv";
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName)) {
            FormatGuesser.Result actual = guesser.guess(inputStream);

            Assert.assertNotNull(actual);
            Assert.assertTrue(actual.getFormatGuess() instanceof CSVFormatGuess);
            char separator = actual.getParameters().get(CSVFormatGuess.SEPARATOR_PARAMETER).charAt(0);
            Assert.assertEquals(separator, ';');
        }
    }
}