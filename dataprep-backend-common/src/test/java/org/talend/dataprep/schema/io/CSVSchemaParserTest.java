package org.talend.dataprep.schema.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.schema.SchemaParser;
import org.talend.dataprep.schema.SchemaParserResult;

/**
 * Unit test for the CSVSchemaParser class.
 * 
 * @see CSVSchemaParser
 */
public class CSVSchemaParserTest {

    /** The parser to test. */
    private CSVSchemaParser parser;

    /**
     * Unit test constructor.
     */
    public CSVSchemaParserTest() {
        parser = new CSVSchemaParser();
    }

    @Test
    public void should_parse_csv() throws IOException {
        String fileName = "org/talend/dataprep/schema/simple.csv";
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName)) {

            DataSetMetadata datasetMetadata = IoTestUtils.getSimpleDataSetMetadata("first name", "last name");

            SchemaParserResult result = parser.parse(new SchemaParser.Request(inputStream, datasetMetadata));
            List<ColumnMetadata> actual = result.getSheetContents().get(0).getColumnMetadatas();

            Assert.assertEquals(datasetMetadata.getRowMetadata().getColumns(), actual);
        }
    }

    /**
     * When trying to guess the columns data type an IndexOutOfBoundsException should not be thrown.
     * @throws IOException
     */
    @Test
    public void TDP_898() throws IOException {
        String str = "c1;c2"+System.lineSeparator()+"1;2;false";
        try (InputStream inputStream = new ByteArrayInputStream(str.getBytes())) {
            DataSetMetadata datasetMetadata = IoTestUtils.getSimpleDataSetMetadata("c1", "c2");
            try {
                SchemaParserResult result = parser.parse(new SchemaParser.Request(inputStream, datasetMetadata));
            }
            catch(IndexOutOfBoundsException exc){
                Assert.fail("Should not throw an IndexOutOfBoundsException, when parsing!");
            }


        }
    }

}
