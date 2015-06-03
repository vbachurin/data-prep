package org.talend.dataprep.schema.io;

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
 * Unit test for the XLSSchemaParser class.
 * 
 * @see XlsSchemaParser
 */
public class XlsSchemaParserTest {

    /** The parser to test. */
    private XlsSchemaParser parser;

    /**
     * Unit test constructor.
     */
    public XlsSchemaParserTest() {
        parser = new XlsSchemaParser();
    }

    @Test
    public void should_parse_xls() throws IOException {
        String fileName = "org/talend/dataprep/schema/simple.xls";
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName)) {

            DataSetMetadata datasetMetadata = IoTestUtils.getSimpleDataSetMetadata("Film", "Producer");

            SchemaParserResult result = parser.parse(new SchemaParser.Request(inputStream, datasetMetadata));
            List<ColumnMetadata> actual = result.getSheetContents().get(0).getColumnMetadatas();

            Assert.assertEquals(datasetMetadata.getRow().getColumns(), actual);
        }
    }

}
