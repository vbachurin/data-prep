package org.talend.dataprep.transformation.api.action.metadata.fillinvalid;

import org.junit.Test;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;
import org.talend.dataprep.transformation.api.action.metadata.ActionMetadataTestUtils;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.talend.dataprep.transformation.api.action.metadata.ActionMetadataTestUtils.getColumn;

/**
 * Unit test for the FillWithStringIfInvalid action.
 * 
 * @see FillWithStringIfInvalid
 */
public class FillWithStringIfInvalidTest {

    /** The action to test. */
    private FillWithStringIfInvalid action;

    /**
     * Default empty constructor.
     */
    public FillWithStringIfInvalidTest() {
        action = new FillWithStringIfInvalid();
    }

    @Test
    public void should_fill_non_valid_string() throws Exception {
        // given
        final Map<String, String> values = new HashMap<>();
        values.put("0001", "David Bowie");
        values.put("0002", "N");
        values.put("0003", "100");

        final RowMetadata rowMetadata = new RowMetadata();
        rowMetadata.setColumns(asList(ColumnMetadata.Builder.column() //
                .type(Type.STRING) //
                .computedId("0003") //
                .invalidValues(newHashSet("100")) //
                .build()));

        final DataSetRow row = new DataSetRow(values);
        row.setRowMetadata(rowMetadata);

        Map<String, String> parameters = ActionMetadataTestUtils.parseParameters(action, //
                this.getClass().getResourceAsStream("fillInvalidStringAction.json"));

        // when
        action.applyOnColumn(row, new TransformationContext(), parameters, "0003");

        // then
        assertEquals("beer", row.get("0003"));
        assertEquals("David Bowie", row.get("0001"));
    }

    @Test
    public void should_accept_column() {
        assertTrue(action.acceptColumn(getColumn(Type.STRING)));
    }

    @Test
    public void should_not_accept_column() {
        assertFalse(action.acceptColumn(getColumn(Type.NUMERIC)));
        assertFalse(action.acceptColumn(getColumn(Type.DOUBLE)));
        assertFalse(action.acceptColumn(getColumn(Type.FLOAT)));
        assertFalse(action.acceptColumn(getColumn(Type.INTEGER)));
        assertFalse(action.acceptColumn(getColumn(Type.DATE)));
        assertFalse(action.acceptColumn(getColumn(Type.BOOLEAN)));
        assertFalse(action.acceptColumn(getColumn(Type.ANY)));
    }
}