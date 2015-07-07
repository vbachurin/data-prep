package org.talend.dataprep.transformation.api.action.metadata;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.talend.dataprep.api.dataset.ColumnMetadata.Builder.column;
import static org.talend.dataprep.transformation.api.action.metadata.ActionMetadataTestUtils.getColumn;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import org.junit.Test;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;

/**
 * Test class for DeleteEmpty action. Creates one consumer, and test it.
 *
 * @see DeleteEmpty
 */
public class DeleteEmptyTest {

    /** The action to test. */
    private DeleteEmpty deleteEmpty;

    /** The consumer out of the action. */
    private BiConsumer<DataSetRow, TransformationContext> consumer;

    /**
     * Default constructor.
     */
    public DeleteEmptyTest() throws IOException {
        deleteEmpty = new DeleteEmpty();

        Map<String, String> parameters = ActionMetadataTestUtils.parseParameters( //
                deleteEmpty, //
                DeleteEmptyTest.class.getResourceAsStream("deleteEmptyAction.json"));

        consumer = deleteEmpty.create(parameters);
    }

    @Test
    public void testAdapt() throws Exception {
        assertThat(deleteEmpty.adapt(null), is(deleteEmpty));
        ColumnMetadata column = column().name("myColumn").id(0).type(Type.STRING).build();
        assertThat(deleteEmpty.adapt(column), is(deleteEmpty));
    }

    @Test
    public void should_delete_because_value_not_set() {
        Map<String, String> values = new HashMap<>();
        values.put("name", "David Bowie");
        DataSetRow dsr = new DataSetRow(values);

        consumer.accept(dsr, new TransformationContext());
        assertTrue(dsr.isDeleted());

        // Assert that action did not change the row values
        assertEquals("David Bowie", dsr.get("name"));
    }

    @Test
    public void should_delete_because_null() {
        Map<String, String> values = new HashMap<>();
        values.put("name", "David Bowie");
        values.put("city", null);
        DataSetRow dsr = new DataSetRow(values);

        consumer.accept(dsr, new TransformationContext());
        assertTrue(dsr.isDeleted());
    }

    @Test
    public void should_delete_because_empty() {
        Map<String, String> values = new HashMap<>();
        values.put("name", "David Bowie");
        values.put("city", "");
        DataSetRow dsr = new DataSetRow(values);

        consumer.accept(dsr, new TransformationContext());
        assertTrue(dsr.isDeleted());
    }

    @Test
    public void should_delete_because_value_is_made_of_spaces() {
        Map<String, String> values = new HashMap<>();
        values.put("name", "David Bowie");
        values.put("city", " ");
        DataSetRow dsr = new DataSetRow(values);

        consumer.accept(dsr, new TransformationContext());
        assertTrue(dsr.isDeleted());
    }

    @Test
    public void should_not_delete_because_value_set() {
        Map<String, String> values = new HashMap<>();
        values.put("name", "David Bowie");
        values.put("city", "-");
        DataSetRow dsr = new DataSetRow(values);

        consumer.accept(dsr, new TransformationContext());
        assertFalse(dsr.isDeleted());

        // Assert that action did not change the row values
        assertEquals("David Bowie", dsr.get("name"));
        assertEquals("-", dsr.get("city"));
    }

    @Test
    public void should_not_delete_because_value_set_2() {
        Map<String, String> values = new HashMap<>();
        values.put("name", "David Bowie");
        values.put("city", " a value ");
        DataSetRow dsr = new DataSetRow(values);

        consumer.accept(dsr, new TransformationContext());
        assertFalse(dsr.isDeleted());
    }

    @Test
    public void should_not_delete_because_value_set_of_boolean() {
        Map<String, String> values = new HashMap<>();
        values.put("name", "David Bowie");
        values.put("city", "true");
        DataSetRow dsr = new DataSetRow(values);

        consumer.accept(dsr, new TransformationContext());
        assertFalse(dsr.isDeleted());
    }

    @Test
    public void should_not_delete_because_value_set_of_number() {
        Map<String, String> values = new HashMap<>();
        values.put("name", "David Bowie");
        values.put("city", "45");
        DataSetRow dsr = new DataSetRow(values);

        consumer.accept(dsr, new TransformationContext());
        assertFalse(dsr.isDeleted());
    }

    @Test
    public void should_not_delete_because_value_set_of_negative_boolean() {
        Map<String, String> values = new HashMap<>();
        values.put("name", "David Bowie");
        values.put("city", "-12");
        DataSetRow dsr = new DataSetRow(values);

        consumer.accept(dsr, new TransformationContext());
        assertFalse(dsr.isDeleted());
    }

    @Test
    public void should_not_delete_because_value_set_of_float() {
        Map<String, String> values = new HashMap<>();
        values.put("name", "David Bowie");
        values.put("city", "0.001");
        DataSetRow dsr = new DataSetRow(values);

        consumer.accept(dsr, new TransformationContext());
        assertFalse(dsr.isDeleted());
    }

    @Test
    public void should_accept_column() {
        assertTrue(deleteEmpty.accept(getColumn(Type.STRING)));
        assertTrue(deleteEmpty.accept(getColumn(Type.NUMERIC)));
        assertTrue(deleteEmpty.accept(getColumn(Type.FLOAT)));
        assertTrue(deleteEmpty.accept(getColumn(Type.DATE)));
        assertTrue(deleteEmpty.accept(getColumn(Type.BOOLEAN)));
        assertTrue(deleteEmpty.accept(getColumn(Type.ANY)));
    }

}
