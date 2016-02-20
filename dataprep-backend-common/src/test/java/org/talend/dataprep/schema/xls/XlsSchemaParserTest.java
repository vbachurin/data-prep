package org.talend.dataprep.schema.xls;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.schema.AbstractSchemaTestUtils;
import org.talend.dataprep.schema.SchemaParser;
import org.talend.dataprep.schema.SchemaParserResult;

/**
 * Unit test for the XLSSchemaParser class.
 * 
 * @see XlsSchemaParser
 */
public class XlsSchemaParserTest extends AbstractSchemaTestUtils {

    /** The parser to test. */
    @Autowired
    private XlsSchemaParser parser;


    @Test
    public void should_parse_xls() throws IOException {
        checkColumnsName("simple.xls", "Film", "Producer");
    }

    /**
     * see https://jira.talendforge.org/browse/TDP-827
     */
    @Test
    public void shouldParseFileWithHeader() throws Exception {
        checkColumnsName("file_with_header.xlsx", "col_0", "col_1", "col_2", "col_3", "col_4");
    }

    /**
     * see https://jira.talendforge.org/browse/TDP-830
     */
    @Test
    public void shouldParseFileWithEmptyColumn() throws Exception {
        checkColumnsName("empty_column.xlsx", "First Name", "Last Name", "Company", "Email Address", "col_5", //
                         "Current Product", //
                         "Product to send"); //
    }

    /**
     * Load the excel file and check the parsed columns name against the given ones.
     *
     * @param sourceFileName the excel file name to load.
     * @param expectedColsName the expected columns name.
     * @throws IOException if an error occurs while reading the excel file.
     */
    private void checkColumnsName(String sourceFileName, String... expectedColsName) throws IOException {
        try (InputStream inputStream = this.getClass().getResourceAsStream(sourceFileName)) {

            DataSetMetadata datasetMetadata = ioTestUtils.getSimpleDataSetMetadata();

            SchemaParserResult result = parser.parse(new SchemaParser.Request(inputStream, datasetMetadata));
            List<ColumnMetadata> columns = result.getSheetContents().get(0).getColumnMetadatas();
            final List<String> actual = columns.stream().map(ColumnMetadata::getName).collect(Collectors.toList());

            Assertions.assertThat( actual ).containsExactly( expectedColsName );

        }
    }
}
