package org.talend.dataprep.schema.io;

import static org.talend.dataprep.test.SameJSONFile.sameJSONAsFile;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.talend.dataprep.api.dataset.DataSetMetadata;

/**
 * Unit test for the CSVSerializer test.
 * 
 * @see CSVSerializer
 */
public class CSVSerializerTest {

    /** The Serializer to test. */
    private CSVSerializer serializer;

    /**
     * Init the serializer to test.
     */
    public CSVSerializerTest() {
        serializer = new CSVSerializer();
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
