package org.talend.dataprep.schema;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    /** This class logger. */
    private final static Logger logger = LoggerFactory.getLogger(LineBasedFormatGuesserTest.class);

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
     * Standard text file.
     */
    @Test
    public void shouldNotGuessCSV() throws IOException {
        String fileName = "org/talend/dataprep/schema/not_a_csv.txt";
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName)) {
            FormatGuesser.Result actual = guesser.guess(inputStream);

            Assert.assertNotNull(actual);
            Assert.assertTrue(actual.getFormatGuess() instanceof NoOpFormatGuess);
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
}