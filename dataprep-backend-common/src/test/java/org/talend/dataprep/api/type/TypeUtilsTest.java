package org.talend.dataprep.api.type;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.talend.dataprep.api.dataset.ColumnMetadata.Builder.column;

import java.util.Collections;

import org.junit.Test;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.datascience.common.inference.type.DataType;

public class TypeUtilsTest {

    @Test
    public void testConvertString() throws Exception {
        ColumnMetadata metadata = column().type(Type.ANY).build();
        DataType.Type[] types = TypeUtils.convert(Collections.singletonList(metadata));
        assertThat(types[0], is(DataType.Type.STRING));

        metadata = column().type(Type.STRING).build();
        types = TypeUtils.convert(Collections.singletonList(metadata));
        assertThat(types[0], is(DataType.Type.STRING));
    }

    @Test
    public void testConvertInteger() throws Exception {
        ColumnMetadata metadata = column().type(Type.NUMERIC).build();
        DataType.Type[] types = TypeUtils.convert(Collections.singletonList(metadata));
        assertThat(types[0], is(DataType.Type.INTEGER));

        metadata = column().type(Type.INTEGER).build();
        types = TypeUtils.convert(Collections.singletonList(metadata));
        assertThat(types[0], is(DataType.Type.INTEGER));
    }

    @Test
    public void testConvertDouble() throws Exception {
        ColumnMetadata metadata = column().type(Type.DOUBLE).build();
        DataType.Type[] types = TypeUtils.convert(Collections.singletonList(metadata));
        assertThat(types[0], is(DataType.Type.DOUBLE));

        metadata = column().type(Type.FLOAT).build();
        types = TypeUtils.convert(Collections.singletonList(metadata));
        assertThat(types[0], is(DataType.Type.DOUBLE));
    }

    @Test
    public void testConvertBoolean() throws Exception {
        ColumnMetadata metadata = column().type(Type.BOOLEAN).build();
        final DataType.Type[] types = TypeUtils.convert(Collections.singletonList(metadata));
        assertThat(types[0], is(DataType.Type.BOOLEAN));
    }

    @Test
    public void testConvertDate() throws Exception {
        ColumnMetadata metadata = column().type(Type.DATE).build();
        final DataType.Type[] types = TypeUtils.convert(Collections.singletonList(metadata));
        assertThat(types[0], is(DataType.Type.DATE));
    }



}
