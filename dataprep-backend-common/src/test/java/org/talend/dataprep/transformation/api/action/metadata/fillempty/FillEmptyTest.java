package org.talend.dataprep.transformation.api.action.metadata.fillempty;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;
import org.talend.dataprep.transformation.api.action.metadata.ActionMetadataTestUtils;

import com.google.common.collect.Sets;

public class FillEmptyTest
{

    @Test
    public void should_fill_non_valid_integer() throws Exception {
        // given
        final Map<String, String> values = new HashMap<>();
        values.put("0001", "David Bowie");
        values.put("0002", "");
        values.put( "0003", "Something" );

        final RowMetadata rowMetadata = new RowMetadata();
        rowMetadata.setColumns( Arrays.asList( ColumnMetadata.Builder.column() //
                                                   .type( Type.INTEGER ) //
                                                   .computedId( "0002" ) //
                                                   .build() ) );

        final DataSetRow row = new DataSetRow(values);
        row.setRowMetadata(rowMetadata);


        FillWithIntegerIfEmpty fillWithIntegerIfEmpty  = new FillWithIntegerIfEmpty();

        Map<String, String>  parameters = ActionMetadataTestUtils.parseParameters( fillWithIntegerIfEmpty, //
                                                                                   FillEmptyTest.class.getResourceAsStream(
                                                                                       "fillInvalidIntegerAction.json" ) );


        // when
        fillWithIntegerIfEmpty.applyOnColumn(row, new TransformationContext(), parameters, "0002");

        // then
        Assert.assertEquals( "25", row.get( "0002" ) );
        Assert.assertEquals( "David Bowie", row.get( "0001" ) );
    }

    @Test
    public void should_fill_non_valid_string() throws Exception{
        // given
        final Map<String, String> values = new HashMap<>();
        values.put("0001", "David Bowie");
        values.put("0002", "");
        values.put("0003", "100");

        final RowMetadata rowMetadata = new RowMetadata();
        rowMetadata.setColumns( Arrays.asList( ColumnMetadata.Builder.column() //
                                                   .type( Type.STRING ) //
                                                   .computedId( "0003" ) //
                                                   .build() ) );

        final DataSetRow row = new DataSetRow(values);
        row.setRowMetadata( rowMetadata );

        FillWithStringIfEmpty fillWithStringIfInvalid  = new FillWithStringIfInvalid();

        Map<String, String>  parameters =  ActionMetadataTestUtils.parseParameters(fillWithStringIfInvalid, //
                                                                                   FillInvalidTest.class.getResourceAsStream("fillInvalidStringAction.json"));

        // when
        fillWithStringIfInvalid.applyOnColumn( row, new TransformationContext(), parameters, "0003" );

        // then
        Assert.assertEquals( "beer", row.get( "0003" ) );
        Assert.assertEquals( "David Bowie", row.get( "0001" ) );
    }

    @Test
    public void should_fill_non_valid_boolean() throws Exception{
        // given
        final Map<String, String> values = new HashMap<>();
        values.put("0001", "David Bowie");
        values.put("0002", "");
        values.put("0003", "100");

        final RowMetadata rowMetadata = new RowMetadata();
        rowMetadata.setColumns( Arrays.asList( ColumnMetadata.Builder.column() //
                                                   .type( Type.BOOLEAN ) //
                                                   .computedId( "0003" ) //
                                                   .build() ) );

        final DataSetRow row = new DataSetRow(values);
        row.setRowMetadata( rowMetadata );

        FillWithStringIfInvalid fillWithStringIfInvalid  = new FillWithStringIfInvalid();

        Map<String, String>  parameters =  ActionMetadataTestUtils.parseParameters(fillWithStringIfInvalid, //
                                                                                   FillInvalidTest.class.getResourceAsStream("fillInvalidBooleanAction.json"));

        // when
        fillWithStringIfInvalid.applyOnColumn( row, new TransformationContext(), parameters, "0003" );

        // then
        Assert.assertEquals( "True", row.get( "0003" ) );
        Assert.assertEquals( "David Bowie", row.get( "0001" ) );
    }

}
