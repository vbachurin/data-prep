package org.talend.dataprep.schema.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.schema.CSVFormatGuess;
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

            DataSetMetadata datasetMetadata = getDataSetMetadata();

            List<ColumnMetadata> expected = new ArrayList<>(2);
            expected.add(getColumnMetadata("0", "Film"));
            expected.add(getColumnMetadata("1", "Producer"));

            SchemaParserResult result = parser.parse(new SchemaParser.Request(inputStream, datasetMetadata));
            List<ColumnMetadata> actual = result.getSheetContents().get(0).getColumnMetadatas();

            Assert.assertEquals(expected, actual);
        }
    }

    /**
     * @return a ready to use dataset.
     */
    private DataSetMetadata getDataSetMetadata() {
        DataSetMetadata datasetMetadata = DataSetMetadata.Builder.metadata().id("123456789").build();
        datasetMetadata.getContent().addParameter(CSVFormatGuess.SEPARATOR_PARAMETER, ";");
        return datasetMetadata;
    }

    /**
     * @param id the column id.
     * @param name the column name.
     * @return the wanted column metadata.
     */
    private ColumnMetadata getColumnMetadata(String id, String name) {
        return ColumnMetadata.Builder.column().id(id).name(name).headerSize(1).type(Type.STRING).build();
    }

}
