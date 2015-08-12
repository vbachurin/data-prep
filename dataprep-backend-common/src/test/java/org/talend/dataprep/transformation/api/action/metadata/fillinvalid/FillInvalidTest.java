package org.talend.dataprep.transformation.api.action.metadata.fillinvalid;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.talend.dataprep.transformation.api.action.metadata.ActionMetadataTestUtils.getColumn;

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

public class FillInvalidTest
{

    private FillWithIntegerIfInvalid fillWithIntegerIfInvalid;

    private FillWithStringIfInvalid fillWithStringIfInvalid;

    private Map<String, String> parameters;

    /**
     * Default constructor.
     */
    public FillInvalidTest() throws IOException {
        fillWithStringIfInvalid = new FillWithStringIfInvalid();
        fillWithIntegerIfInvalid = new FillWithIntegerIfInvalid();

        parameters = ActionMetadataTestUtils.parseParameters( fillWithIntegerIfInvalid, //
                FillInvalidTest.class.getResourceAsStream("fillInvalidAction.json"));

    }

    @Test
    public void should_fill_non_valid() {
        //given
        final Map<String, String> values = new HashMap<>();
        values.put("0001", "David Bowie");
        values.put("0002", "N");
        values.put("0003", "Something");

        final RowMetadata rowMetadata = new RowMetadata();
        rowMetadata.setColumns(asList(ColumnMetadata.Builder.column() //
                .type(Type.INTEGER) //
                .computedId("0002") //
                .invalidValues(newHashSet("N")) //
                .build()));

        final DataSetRow row = new DataSetRow(values);
        row.setRowMetadata(rowMetadata);

        //when
        fillWithIntegerIfInvalid.applyOnColumn( row, new TransformationContext(), parameters, "0002" );

        //then
        assertEquals("0", row.get( "0002" ));
        assertEquals("David Bowie", row.get("0001"));
    }

}
