package org.talend.dataprep.schema.io;

import static org.talend.dataprep.test.SameJSONFile.sameJSONAsFile;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.talend.dataprep.api.dataset.DataSetMetadata;

/**
 * Unit test for the CSVSerializer test.
 * 
 * @see CSVSerializer
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CSVSerializerTest.class)
@Configuration
@ComponentScan(basePackages = "org.talend.dataprep")
@EnableAutoConfiguration
public class CSVSerializerTest {

    @Autowired
    private ApplicationContext context;

    /** The Serializer to test. */
    private CSVSerializer serializer;

    @Before
    public void setUp() throws Exception {
        serializer = context.getBean(CSVSerializer.class);
    }

    @Test
    public void should_serialize_standard_csv() throws IOException {
        String fileName = "org/talend/dataprep/schema/simple.csv";
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
        DataSetMetadata datasetMetadata = IoTestUtils.getSimpleDataSetMetadata("first name", "last name");

        InputStream input = serializer.serialize(inputStream, datasetMetadata);
        String actual = IOUtils.toString(input);

        InputStream expected = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("org/talend/dataprep/schema/simple.csv_expected.json");
        Assert.assertThat(actual, sameJSONAsFile(expected));
    }

    @Test
    public void should_serialize_csv_with_missing_values() throws IOException {
        String fileName = "org/talend/dataprep/schema/missing_values.csv";
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
        DataSetMetadata datasetMetadata = IoTestUtils.getSimpleDataSetMetadata("character", "actor", "active");

        InputStream input = serializer.serialize(inputStream, datasetMetadata);
        String actual = IOUtils.toString(input);

        InputStream expected = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("org/talend/dataprep/schema/missing_values.csv_expected.json");
        Assert.assertThat(actual, sameJSONAsFile(expected));
    }

    @Test
    public void should_serialize_csv_with_additional_values() throws IOException {
        String fileName = "org/talend/dataprep/schema/additional_values.csv";
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
        DataSetMetadata datasetMetadata = IoTestUtils.getSimpleDataSetMetadata("name", "email");

        InputStream input = serializer.serialize(inputStream, datasetMetadata);
        String actual = IOUtils.toString(input);

        InputStream expected = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("org/talend/dataprep/schema/additional_values.csv_expected.json");
        Assert.assertThat(actual, sameJSONAsFile(expected));
    }
}
