package org.talend.dataprep.api.dataset;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for the ColumnMetadata class.
 * 
 * @see ColumnMetadata
 */
public class ColumnMetadataTest {

    @Test
    public void should_generate_id() {
        ColumnMetadata metadata = new ColumnMetadata();
        Assert.assertNotNull(metadata.getId());
    }

    @Test
    public void should_use_given_id() {
        String id = "ID#123456789";
        ColumnMetadata metadata = new ColumnMetadata(id, "name", "String");
        Assert.assertEquals(id, metadata.getId());
    }
}
