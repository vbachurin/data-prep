package org.talend.dataprep.dataset.objects;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.talend.dataprep.api.ColumnMetadata.Builder.*;
import static org.talend.dataprep.api.DataSetMetadata.Builder.*;

import java.util.List;
import java.util.UUID;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.talend.dataprep.api.ColumnMetadata;
import org.talend.dataprep.api.DataSetMetadata;
import org.talend.dataprep.api.type.Type;

public class DataSetBuilderTest {

    @Test
    public void testDataSetMetadataBuild() {
        String dataSetId = UUID.randomUUID().toString();
        DataSetMetadata dataSetMetadata = metadata().id(dataSetId)
                .row(column().name("firstname").type(Type.STRING), column().name("lastname").type(Type.STRING)).build();
        assertThat(dataSetMetadata.getId(), is(dataSetId));
        List<ColumnMetadata> columns = dataSetMetadata.getRow().getColumns();
        assertThat(columns.size(), is(2));
        assertThat(columns, CoreMatchers.hasItems(column().name("firstname").type(Type.STRING).build()));
        assertThat(columns, CoreMatchers.hasItems(column().name("lastname").type(Type.STRING).build()));
    }

}
