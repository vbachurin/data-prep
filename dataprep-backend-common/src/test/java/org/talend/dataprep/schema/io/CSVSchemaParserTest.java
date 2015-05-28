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

            DataSetMetadata datasetMetadata = getDataSetMetadata();

            List<ColumnMetadata> expected = new ArrayList<>(2);
            expected.add(getColumnMetadata("0", "first name"));
            expected.add(getColumnMetadata("1", "last name"));

            List<ColumnMetadata> actual = parser.parse(inputStream, datasetMetadata);

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
        return ColumnMetadata.Builder.column().id(id).name(name).type(Type.STRING).build();
    }

}
