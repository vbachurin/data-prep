package org.talend.dataprep.api.dataset.json;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.schema.CSVFormatGuess;
import org.talend.dataprep.schema.Separator;
import org.talend.dataprep.schema.XlsFormatGuess;
import org.talend.dataprep.test.SameJSONFile;

public class DataSetMetadataJSONTest {

    @Test
    public void testArguments() throws Exception {
        DataSetMetadata metadata = DataSetMetadata.from(null);
        assertNull(metadata);
        metadata = DataSetMetadata.from(new ByteArrayInputStream(new byte[0]));
        assertNull(metadata);
    }

    @Test
    public void testRead1() throws Exception {
        DataSetMetadata metadata = DataSetMetadata.from(DataSetMetadataJSONTest.class.getResourceAsStream("test1.json"));
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
        metadata.getContent().setContentType(new CSVFormatGuess(new Separator()));
        metadata.getLifecycle().qualityAnalyzed(true);
        metadata.getLifecycle().schemaAnalyzed(true);
        StringWriter writer = new StringWriter();
        metadata.to(writer);
        assertThat(writer.toString(), SameJSONFile.sameJSONAsFile(DataSetMetadataJSONTest.class.getResourceAsStream("test2.json")));
    }

    @Test
    public void testRoundTrip() throws Exception {
        DataSetMetadata metadata = DataSetMetadata.from(DataSetMetadataJSONTest.class.getResourceAsStream("test3.json"));
        metadata.getContent().setContentType(new CSVFormatGuess(new Separator()));
        assertNotNull(metadata);
        StringWriter writer = new StringWriter();
        metadata.to(writer);
        assertThat(writer.toString(), SameJSONFile.sameJSONAsFile(DataSetMetadataJSONTest.class.getResourceAsStream("test3.json")));
    }

}
