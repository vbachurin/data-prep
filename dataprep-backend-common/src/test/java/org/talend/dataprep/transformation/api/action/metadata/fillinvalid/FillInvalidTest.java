package org.talend.dataprep.transformation.api.action.metadata.fillinvalid;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;
import org.talend.dataprep.transformation.api.action.metadata.ActionMetadataTestUtils;

public class FillInvalidTest {

    @Test
    public void should_fill_non_valid_integer() throws Exception {
        // given
        final Map<String, String> values = new HashMap<>();
        values.put("0001", "David Bowie");
        values.put("0002", "N");
        values.put( "0003", "Something" );

        final RowMetadata rowMetadata = new RowMetadata();
        rowMetadata.setColumns( asList( ColumnMetadata.Builder.column() //
                                            .type( Type.INTEGER ) //
                                            .computedId( "0002" ) //
                                            .invalidValues( newHashSet( "N" ) ) //
                                            .build() ) );

        final DataSetRow row = new DataSetRow(values);
        row.setRowMetadata(rowMetadata);


        FillWithIntegerIfInvalid fillWithIntegerIfInvalid  = new FillWithIntegerIfInvalid();

        Map<String, String>  parameters = ActionMetadataTestUtils.parseParameters(fillWithIntegerIfInvalid, //
                                                                        FillInvalidTest.class.getResourceAsStream("fillInvalidIntegerAction.json"));


        // when
        fillWithIntegerIfInvalid.applyOnColumn(row, new TransformationContext(), parameters, "0002");

        // then
        assertEquals("25", row.get("0002"));
        assertEquals("David Bowie", row.get("0001"));
    }

    @Test
    public void should_fill_non_valid_string() throws Exception{
        // given
        final Map<String, String> values = new HashMap<>();
        values.put("0001", "David Bowie");
        values.put("0002", "N");
        values.put("0003", "100");

        final RowMetadata rowMetadata = new RowMetadata();
        rowMetadata.setColumns( asList( ColumnMetadata.Builder.column() //
                                            .type( Type.STRING ) //
                                            .computedId( "0003" ) //
                                            .invalidValues( newHashSet( "100" ) ) //
                                            .build() ) );

        final DataSetRow row = new DataSetRow(values);
        row.setRowMetadata( rowMetadata );

        FillWithStringIfInvalid fillWithStringIfInvalid  = new FillWithStringIfInvalid();

        Map<String, String>  parameters =  ActionMetadataTestUtils.parseParameters(fillWithStringIfInvalid, //
                                                                                   FillInvalidTest.class.getResourceAsStream("fillInvalidStringAction.json"));

            // when
        fillWithStringIfInvalid.applyOnColumn( row, new TransformationContext(), parameters, "0003" );

        // then
        assertEquals("beer", row.get("0003"));
        assertEquals("David Bowie", row.get("0001"));
    }

    @Test
    public void should_fill_non_valid_boolean() throws Exception{
        // given
        final Map<String, String> values = new HashMap<>();
        values.put("0001", "David Bowie");
        values.put("0002", "N");
        values.put("0003", "100");

        final RowMetadata rowMetadata = new RowMetadata();
        rowMetadata.setColumns( asList( ColumnMetadata.Builder.column() //
                                            .type( Type.BOOLEAN ) //
                                            .computedId( "0003" ) //
                                            .invalidValues( newHashSet( "100" ) ) //
                                            .build() ) );

        final DataSetRow row = new DataSetRow(values);
        row.setRowMetadata( rowMetadata );

        FillWithStringIfInvalid fillWithStringIfInvalid  = new FillWithStringIfInvalid();

        Map<String, String>  parameters =  ActionMetadataTestUtils.parseParameters(fillWithStringIfInvalid, //
                                                                                   FillInvalidTest.class.getResourceAsStream("fillInvalidBooleanAction.json"));

        // when
        fillWithStringIfInvalid.applyOnColumn( row, new TransformationContext(), parameters, "0003" );

        // then
        assertEquals("True", row.get("0003"));
        assertEquals("David Bowie", row.get("0001"));
    }

}
