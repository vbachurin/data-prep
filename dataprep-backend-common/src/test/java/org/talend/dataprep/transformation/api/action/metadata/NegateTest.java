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
 * Test class for Negate action.
 * 
 * @see Negate
 */
public class NegateTest {

    /** The row consumer to test. */
    private BiConsumer<DataSetRow, TransformationContext> consumer;

    /** The action to test. */
    private Negate action;

    /**
     * Default empty constructor
     */
    public NegateTest() throws IOException {
        action = new Negate();

        Map<String, String> parameters = ActionMetadataTestUtils.parseParameters( //
                action, //
                NegateTest.class.getResourceAsStream("negateAction.json"));

        consumer = action.create(parameters).getRowAction();
    }

    @Test
    public void testAdapt() throws Exception {
        assertThat(action.adapt(null), is(action));
        ColumnMetadata column = column().name("myColumn").id(0).type(Type.STRING).build();
        assertThat(action.adapt(column), is(action));
    }

    @Test
    public void should_negate_true() {
        Map<String, String> values = new HashMap<>();
        values.put("name", "Vincent");
        values.put("entity", "R&D");
        values.put("active", "true");
        DataSetRow row = new DataSetRow(values);

        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("name", "Vincent");
        expectedValues.put("entity", "R&D");
        expectedValues.put("active", "False"); // true -> false

        consumer.accept(row, new TransformationContext());
        assertEquals(expectedValues, row.values());
    }

    @Test
    public void should_negate_false() {
        Map<String, String> values = new HashMap<>();
        values.put("name", "Vincent");
        values.put("entity", "R&D");
        values.put("active", "false");
        DataSetRow row = new DataSetRow(values);

        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("name", "Vincent");
        expectedValues.put("entity", "R&D");
        expectedValues.put("active", "True"); // false -> true

        consumer.accept(row, new TransformationContext());
        assertEquals(expectedValues, row.values());
    }

    @Test
    public void should_accept_column() {
        assertTrue(action.accept(getColumn(Type.BOOLEAN)));
    }

    @Test
    public void should_not_accept_column() {
        assertFalse(action.accept(getColumn(Type.NUMERIC)));
        assertFalse(action.accept(getColumn(Type.FLOAT)));
        assertFalse(action.accept(getColumn(Type.DATE)));
        assertFalse(action.accept(getColumn(Type.STRING)));
    }
}
