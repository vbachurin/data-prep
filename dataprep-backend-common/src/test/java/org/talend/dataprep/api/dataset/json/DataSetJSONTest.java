package org.talend.dataprep.api.dataset.json;

import static org.junit.Assert.*;
import static org.talend.dataprep.test.SameJSONFile.sameJSONAsFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.talend.dataprep.api.dataset.*;
import org.talend.dataprep.exception.CommonErrorCodes;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.schema.CSVFormatGuess;
import org.talend.dataprep.schema.Separator;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DataSetJSONTest.class)
@Configuration
@ComponentScan(basePackages = "org.talend.dataprep")
@EnableAutoConfiguration
public class DataSetJSONTest {

    @Autowired
    Jackson2ObjectMapperBuilder builder;

    /**
     * @param json A valid JSON stream, may be <code>null</code>.
     * @return The {@link DataSetMetadata} instance parsed from stream or <code>null</code> if parameter is null. If
     * stream is empty, also returns <code>null</code>.
     */
    public DataSet from(InputStream json) {
        try {
            return builder.build().reader(DataSet.class).readValue(json);
        } catch (Exception e) {
            throw new TDPException(CommonErrorCodes.UNABLE_TO_PARSE_JSON, e);
        }
    }

    /**
     * Writes the current {@link DataSetMetadata} to <code>writer</code> as JSON format.
     *
     * @param writer A non-null writer.
     */
    public void to(DataSet dataSet, Writer writer) {
        if (writer == null) {
            throw new IllegalArgumentException("Writer cannot be null.");
        }
        try {
            builder.build().writer().writeValue(writer, dataSet);
        } catch (Exception e) {
            throw new TDPException(CommonErrorCodes.UNABLE_TO_SERIALIZE_TO_JSON, e);
        }
    }

    @Test
    public void testArguments() throws Exception {
        try {
            from(null);
            fail("Expected an JSON parse exception.");
        } catch (Exception e) {
            assertEquals(CommonErrorCodes.UNABLE_TO_PARSE_JSON.toString(), e.getMessage());
        }
        try {
            from(new ByteArrayInputStream(new byte[0]));
            fail("Expected an JSON parse exception.");
        } catch (Exception e) {
            assertEquals(CommonErrorCodes.UNABLE_TO_PARSE_JSON.toString(), e.getMessage());
        }
    }

    @Test
    public void testRead1() throws Exception {
        DataSet dataSet = from(DataSetJSONTest.class.getResourceAsStream("test1.json"));
        assertNotNull(dataSet);
        final DataSetMetadata metadata = dataSet.getMetadata();
        assertEquals("410d2196-8f90-478f-a817-7e8b6694ac91", metadata.getId());
        assertEquals("test", metadata.getName());
        assertEquals("anonymousUser", metadata.getAuthor());
        assertEquals(2, metadata.getContent().getNbRecords());
        assertEquals(1, metadata.getContent().getNbLinesInHeader());
        assertEquals(0, metadata.getContent().getNbLinesInFooter());
        final SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-YYYY HH:mm");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date expectedDate = dateFormat.parse("02-17-2015 09:02");
        assertEquals(expectedDate, new Date(metadata.getCreationDate()));
        List<ColumnMetadata> columns = dataSet.getColumns();
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
        final DataSetContent content = metadata.getContent();
        content.addParameter(CSVFormatGuess.SEPARATOR_PARAMETER, Character.toString(new Separator().separator));
        content.setFormatGuessId(new CSVFormatGuess().getBeanId());
        content.setMediaType("text/csv");
        metadata.getLifecycle().qualityAnalyzed(true);
        metadata.getLifecycle().schemaAnalyzed(true);
        StringWriter writer = new StringWriter();
        DataSet dataSet = new DataSet();
        dataSet.setMetadata(metadata);
        dataSet.setColumns(metadata.getRow().getColumns());
        to(dataSet, writer);
        assertThat(writer.toString(), sameJSONAsFile(DataSetJSONTest.class.getResourceAsStream("test2.json")));
    }

    @Test
    public void testRoundTrip() throws Exception {
        DataSet dataSet = from(DataSetJSONTest.class.getResourceAsStream("test3.json"));
        final DataSetMetadata metadata = dataSet.getMetadata();
        metadata.getContent().addParameter(CSVFormatGuess.SEPARATOR_PARAMETER, Character.toString(new Separator().separator));
        metadata.getContent().setFormatGuessId(new CSVFormatGuess().getBeanId());
        assertNotNull(metadata);
        StringWriter writer = new StringWriter();
        to(dataSet, writer);
        assertThat(writer.toString(), sameJSONAsFile(DataSetJSONTest.class.getResourceAsStream("test3.json")));
    }

}
