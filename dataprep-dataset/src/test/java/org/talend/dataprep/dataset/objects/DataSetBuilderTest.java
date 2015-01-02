package org.talend.dataprep.dataset.objects;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.talend.dataprep.dataset.objects.type.Types;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.talend.dataprep.dataset.objects.ColumnMetadata.Builder.column;
import static org.talend.dataprep.dataset.objects.DataSetMetadata.Builder.id;

public class DataSetBuilderTest {

    @Test
    public void testDataSetMetadataBuild() {
        String dataSetId = UUID.randomUUID().toString();
        DataSetMetadata dataSetMetadata = id(dataSetId).row(column().name("firstname").type(Types.STRING),
                column().name("lastname").type(Types.STRING)).build();
        assertThat(dataSetMetadata.getId(), is(dataSetId));
        List<ColumnMetadata> columns = dataSetMetadata.getRow().getColumns();
        assertThat(columns.size(), is(2));
        assertThat(columns, CoreMatchers.hasItems(column().name("firstname").type(Types.STRING).build()));
        assertThat(columns, CoreMatchers.hasItems(column().name("lastname").type(Types.STRING).build()));
    }

}
