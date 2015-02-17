package org.talend.dataprep.api.json;

import org.junit.Ignore;
import org.junit.Test;
import org.talend.dataprep.api.ColumnMetadata;
import org.talend.dataprep.api.DataSetMetadata;
import org.talend.dataprep.api.RowMetadata;
import org.talend.dataprep.test.SameJSONFile;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

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
        DataSetMetadata metadata = DataSetMetadata.from(DataSetMetadata.class.getResourceAsStream("test1.json"));
        assertNotNull(metadata);
        assertEquals("410d2196-8f90-478f-a817-7e8b6694ac91", metadata.getId());
        assertEquals("test", metadata.getName());
        assertEquals("anonymousUser", metadata.getAuthor());
        assertEquals(2, metadata.getContent().getNbRecords());
        assertEquals(1, metadata.getContent().getNbLinesInHeader());
        assertEquals(0, metadata.getContent().getNbLinesInFooter());
        Date expectedDate = DataSetMetadataJsonSerializer.DATE_FORMAT.parse("02-17-2015 09:02");
        assertEquals(expectedDate, metadata.getCreationDate());
        List<ColumnMetadata> columns = metadata.getRow().getColumns();
        assertEquals(6, columns.size());
        ColumnMetadata firstColumn = columns.get(0);
        assertEquals("id", firstColumn.getName());
        assertEquals("integer", firstColumn.getTypeName());
        assertEquals(20, firstColumn.getQuality().getEmpty());
        assertEquals(26, firstColumn.getQuality().getInvalid());
        assertEquals(54, firstColumn.getQuality().getValid());
        ColumnMetadata lastColumn = columns.get(5);
        assertEquals("alive", lastColumn.getName());
        assertEquals("string", lastColumn.getTypeName());
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
        metadata.getLifecycle().qualityAnalyzed(true);
        metadata.getLifecycle().schemaAnalyzed(true);
        StringWriter writer = new StringWriter();
        metadata.to(writer);
        assertThat(writer.toString(), SameJSONFile.sameJSONAsFile(DataSetMetadata.class.getResourceAsStream("test2.json")));
    }

    @Test
    public void testRoundTrip() throws Exception {
        DataSetMetadata metadata = DataSetMetadata.from(DataSetMetadata.class.getResourceAsStream("test3.json"));
        assertNotNull(metadata);
        StringWriter writer = new StringWriter();
        metadata.to(writer);
        assertThat(writer.toString(), SameJSONFile.sameJSONAsFile(DataSetMetadata.class.getResourceAsStream("test3.json")));
    }

}
