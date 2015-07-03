package org.talend.dataprep.api.dataset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.talend.dataprep.api.type.Type;

import uk.co.datumedge.hamcrest.json.SameJSONAs;

/**
 * Unit test for the ColumnMetadata class.
 * 
 * @see ColumnMetadata
 */
public class ColumnMetadataTest {

    @Test
    public void should_format_id() {
        int id = 3;
        String expectedId = "0003";
        ColumnMetadata metadata = ColumnMetadata.Builder.column().id(id).name("name").type(Type.STRING).build();
        assertEquals(expectedId, metadata.getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_not_allow_empty_id() {
        ColumnMetadata.Builder.column().type(Type.STRING).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_not_allow_empty_Type() {
        ColumnMetadata.Builder.column().type(null).build();
    }

    @Test
    public void should_set_empty_statistics() {
        ColumnMetadata column = ColumnMetadata.Builder.column().id(42).type(Type.STRING).build();
        column.setStatistics(null);
        assertEquals("{}", column.getStatistics());
    }

    @Test
    public void should_set_statistics_as_Map() {
        ColumnMetadata column = ColumnMetadata.Builder.column().id(42).type(Type.STRING).build();
        Map statistics = new HashMap<String, String>();
        statistics.put("key", "value");
        column.setStatistics(statistics);
        assertThat(column.getStatistics(), SameJSONAs.sameJSONAs("{key:value}"));
    }

    @Test
    public void should_set_statistics_as_String() {
        ColumnMetadata column = ColumnMetadata.Builder.column().id(42).type(Type.STRING).build();
        String statistics = "{key:value}";
        column.setStatistics(statistics);
        assertEquals(statistics, column.getStatistics());
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_not_accept_any_statistics() {
        ColumnMetadata column = ColumnMetadata.Builder.column().id(42).type(Type.STRING).build();
        column.setStatistics(12);
    }

}
