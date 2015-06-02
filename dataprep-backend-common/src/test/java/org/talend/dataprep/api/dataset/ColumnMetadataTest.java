package org.talend.dataprep.api.dataset;

import org.junit.Assert;
import org.junit.Test;
import org.talend.dataprep.api.type.Type;

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
        Assert.assertEquals(expectedId, metadata.getId());
    }
}
