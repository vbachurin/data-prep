package org.talend.dataprep.api.dataset.json;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.exception.CommonMessages;
import org.talend.dataprep.exception.Exceptions;
import org.talend.dataprep.schema.CSVFormatGuess;
import org.talend.dataprep.schema.Separator;
import org.talend.dataprep.test.SameJSONFile;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DataSetMetadataJSONTest.class)
@Configuration
@ComponentScan(basePackages = "org.talend.dataprep")
@EnableAutoConfiguration
public class DataSetMetadataJSONTest {

    @Autowired
    Jackson2ObjectMapperBuilder builder;

    /**
     * @param json A valid JSON stream, may be <code>null</code>.
     * @return The {@link DataSetMetadata} instance parsed from stream or <code>null</code> if parameter is null. If
     * stream is empty, also returns <code>null</code>.
     */
    public DataSetMetadata from(InputStream json) {
        try {
            return builder.build().reader(DataSetMetadata.class).readValue(json);
        } catch (Exception e) {
            throw Exceptions.User(CommonMessages.UNABLE_TO_PARSE_JSON, e);
        }
    }

    /**
     * Writes the current {@link DataSetMetadata} to <code>writer</code> as JSON format.
     *
     * @param writer A non-null writer.
     */
    public void to(DataSetMetadata dataSetMetadata, Writer writer) {
        if (writer == null) {
            throw new IllegalArgumentException("Writer cannot be null.");
        }
        try {
            builder.build().writer().writeValue(writer, dataSetMetadata);
        } catch (Exception e) {
            throw Exceptions.User(CommonMessages.UNABLE_TO_SERIALIZE_TO_JSON, e);
        }
    }

    @Test
    public void testArguments() throws Exception {
        try {
            from(null);
            fail("Expected an JSON parse exception.");
        } catch (Exception e) {
            assertEquals(CommonMessages.UNABLE_TO_PARSE_JSON.toString(), e.getMessage());
        }
        try {
            from(new ByteArrayInputStream(new byte[0]));
            fail("Expected an JSON parse exception.");
        } catch (Exception e) {
            assertEquals(CommonMessages.UNABLE_TO_PARSE_JSON.toString(), e.getMessage());
        }
    }

    @Test
    public void testRead1() throws Exception {
        DataSetMetadata metadata = from(DataSetMetadataJSONTest.class.getResourceAsStream("test1.json"));
        assertNotNull(metadata);
        assertEquals("410d2196-8f90-478f-a817-7e8b6694ac91", metadata.getId());
        assertEquals("test", metadata.getName());
        assertEquals("anonymousUser", metadata.getAuthor());
        assertEquals(2, metadata.getContent().getNbRecords());
        assertEquals(1, metadata.getContent().getNbLinesInHeader());
        assertEquals(0, metadata.getContent().getNbLinesInFooter());
        Date expectedDate = SimpleDataSetMetadataJsonSerializer.DATE_FORMAT.parse("02-17-2015 09:02");
        assertEquals(expectedDate, metadata.getCreationDate());
        List<ColumnMetadata> columns = metadata.getRow().getColumns();
        assertEquals(6, columns.size());
        ColumnMetadata firstColumn = columns.get(0);
        assertEquals("id", firstColumn.getId());
        assertEquals("integer", firstColumn.getType());
        assertEquals(20, firstColumn.getQuality().getEmpty());
        assertEquals(26, firstColumn.getQuality().getInvalid());
        assertEquals(54, firstColumn.getQuality().getValid());
        ColumnMetadata lastColumn = columns.get(5);
        assertEquals("alive", lastColumn.getId());
        assertEquals("string", lastColumn.getType());
        assertEquals(8, lastColumn.getQuality().getEmpty());
        assertEquals(25, lastColumn.getQuality().getInvalid());
        assertEquals(67, lastColumn.getQuality().getValid());
    }

    @Test
    public void testWrite1() throws Exception {
        List<ColumnMetadata> columns = new ArrayList<>();
        ColumnMetadata column = new ColumnMetadata("column1", "string");
        column.getQuality().setEmpty(0);
        column.getQuality().setInvalid(10);
        column.getQuality().setValid(50);
        columns.add(column);
        RowMetadata row = new RowMetadata(columns);
        DataSetMetadata metadata = new DataSetMetadata("1234", "name", "author", 0, row);
        metadata.getContent().addParameter( CSVFormatGuess.SEPARATOR_PARAMETER, Character.toString(new Separator().separator) );
        metadata.getContent().setFormatGuessId( new CSVFormatGuess().getBeanId() );
        metadata.getLifecycle().qualityAnalyzed(true);
        metadata.getLifecycle().schemaAnalyzed(true);
        StringWriter writer = new StringWriter();
        to(metadata, writer );
        assertThat(writer.toString(), SameJSONFile.sameJSONAsFile(DataSetMetadataJSONTest.class.getResourceAsStream("test2.json")));
    }

    @Test
    public void testRoundTrip() throws Exception {
        DataSetMetadata metadata = from( DataSetMetadataJSONTest.class.getResourceAsStream( "test3.json" ) );
        metadata.getContent().addParameter( CSVFormatGuess.SEPARATOR_PARAMETER, Character.toString(new Separator().separator) );
        metadata.getContent().setFormatGuessId( new CSVFormatGuess().getBeanId() );
        assertNotNull(metadata);
        StringWriter writer = new StringWriter();
        to(metadata, writer );
        assertThat(writer.toString(), SameJSONFile.sameJSONAsFile(DataSetMetadataJSONTest.class.getResourceAsStream("test3.json")));
    }

}
