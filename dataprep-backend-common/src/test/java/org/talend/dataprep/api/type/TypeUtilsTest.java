package org.talend.dataprep.api.type;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.talend.dataprep.api.dataset.ColumnMetadata.Builder.column;

import java.util.Collections;

import org.junit.Test;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum;
import org.talend.dataquality.semantic.classifier.custom.UserDefinedCategory;
import org.talend.dataquality.semantic.recognizer.CategoryFrequency;
import org.talend.datascience.common.inference.semantic.SemanticType;
import org.talend.datascience.common.inference.type.DataType;

public class TypeUtilsTest {

    @Test
    public void testConvertString() throws Exception {
        ColumnMetadata metadata = column().id(1).type(Type.ANY).build();
        DataType.Type[] types = TypeUtils.convert(Collections.singletonList(metadata));
        assertThat(types[0], is(DataType.Type.STRING));

        metadata = column().id(2).type(Type.STRING).build();
        types = TypeUtils.convert(Collections.singletonList(metadata));
        assertThat(types[0], is(DataType.Type.STRING));
    }

    @Test
    public void testConvertInteger() throws Exception {
        ColumnMetadata metadata = column().id(1).type(Type.NUMERIC).build();
        DataType.Type[] types = TypeUtils.convert(Collections.singletonList(metadata));
        assertThat(types[0], is(DataType.Type.INTEGER));

        metadata = column().id(2).type(Type.INTEGER).build();
        types = TypeUtils.convert(Collections.singletonList(metadata));
        assertThat(types[0], is(DataType.Type.INTEGER));
    }

    @Test
    public void testConvertDouble() throws Exception {
        ColumnMetadata metadata = column().id(1).type(Type.DOUBLE).build();
        DataType.Type[] types = TypeUtils.convert(Collections.singletonList(metadata));
        assertThat(types[0], is(DataType.Type.DOUBLE));

        metadata = column().id(2).type(Type.FLOAT).build();
        types = TypeUtils.convert(Collections.singletonList(metadata));
        assertThat(types[0], is(DataType.Type.DOUBLE));
    }

    @Test
    public void testConvertBoolean() throws Exception {
        ColumnMetadata metadata = column().id(1).type(Type.BOOLEAN).build();
        final DataType.Type[] types = TypeUtils.convert(Collections.singletonList(metadata));
        assertThat(types[0], is(DataType.Type.BOOLEAN));
    }

    @Test
    public void testConvertDate() throws Exception {
        ColumnMetadata metadata = column().id(1).type(Type.DATE).build();
        final DataType.Type[] types = TypeUtils.convert(Collections.singletonList(metadata));
        assertThat(types[0], is(DataType.Type.DATE));
    }

    @Test
    public void testNullSemanticDomainType() throws Exception {
        assertThat(TypeUtils.getDomainLabel(((SemanticType) null)), is(""));
        assertThat(TypeUtils.getDomainLabel(((String) null)), is(""));
    }

    @Test
    public void testSemanticDomainType() throws Exception {
        final SemanticType semanticType = new SemanticType();
        semanticType.increment(new CategoryFrequency(new UserDefinedCategory(SemanticCategoryEnum.AIRPORT.getId())), 1);
        assertThat(TypeUtils.getDomainLabel(semanticType), is(SemanticCategoryEnum.AIRPORT.getDisplayName()));
        assertThat(TypeUtils.getDomainLabel(SemanticCategoryEnum.AIRPORT.getId()), is(SemanticCategoryEnum.AIRPORT.getDisplayName()));
    }
}
