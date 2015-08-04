package org.talend.dataprep.transformation.api.action.metadata.delete;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.talend.dataprep.transformation.api.action.metadata.ActionMetadataTestUtils.getColumn;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.DataSetRowAction;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;
import org.talend.dataprep.transformation.api.action.metadata.ActionMetadataTestUtils;

import com.google.common.collect.Sets;

/**
 * Test class for DeleteInvalid action. Creates one consumer, and test it.
 *
 * @see DeleteInvalid
 */
public class DeleteInvalidTest {

    /** The action to test. */
    private DeleteInvalid deleteInvalid;

    /** The consumer out of the action. */
    private DataSetRowAction consumer;

    /**
     * Default constructor.
     */
    public DeleteInvalidTest() throws IOException {
        deleteInvalid = new DeleteInvalid();

        Map<String, String> parameters = ActionMetadataTestUtils.parseParameters(deleteInvalid, //
                DeleteInvalidTest.class.getResourceAsStream("deleteInvalidAction.json"));

        consumer = deleteInvalid.create(parameters).getRowAction();
    }

    @Test
    public void should_delete_because_non_valid() {
        Map<String, String> values = new HashMap<>();
        values.put("0001", "David Bowie");
        values.put("0002", "N");
        values.put("0003", "Something");
        DataSetRow dsr = new DataSetRow(values);
        RowMetadata rowMetadata = new RowMetadata();
        dsr.setRowMetadata(rowMetadata);

        rowMetadata.setColumns(Arrays.asList(ColumnMetadata.Builder.column() //
                .type(Type.INTEGER).computedId("0002") //
                .invalidValues(Sets.newHashSet("N")).build()));

        dsr = consumer.apply(dsr, new TransformationContext());
        assertTrue(dsr.isDeleted());

        // Assert that action did not change the row values
        assertEquals("David Bowie", dsr.get("0001"));
    }

    @Test
    public void should_accept_column() {

        for (Type type : Type.values()) {
            assertTrue(deleteInvalid.acceptColumn(getColumn(type)));
        }

    }

}
