// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprep.transformation.api.action.metadata.delete;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.talend.dataprep.api.dataset.ColumnMetadata.Builder.column;
import static org.talend.dataprep.transformation.api.action.metadata.ActionMetadataTestUtils.getColumn;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.DataSetRowAction;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;
import org.talend.dataprep.transformation.api.action.metadata.ActionMetadataTestUtils;
import org.talend.dataprep.transformation.api.action.metadata.category.ActionCategory;

/**
 * Test class for DeleteOnValue action. Creates one consumer, and test it.
 *
 * @see DeleteOnValue
 */
public class DeleteOnValueTest {

    /** The action to test. */
    DeleteOnValue action;

    /** the action out of the consumer. */
    private DataSetRowAction consumer;

    /**
     * Constructor.
     */
    public DeleteOnValueTest() throws IOException {

        action = new DeleteOnValue();

        Map<String, String> parameters = ActionMetadataTestUtils.parseParameters( //
                action, //
                DeleteOnValueTest.class.getResourceAsStream("deleteOnValueAction.json"));

        consumer = action.create(parameters).getRowAction();
    }

    @Test
    public void testAdapt() throws Exception {
        assertThat(action.adapt(null), is(action));
        ColumnMetadata column = column().name("myColumn").id(0).type(Type.STRING).build();
        assertThat(action.adapt(column), is(action));
    }

    @Test
    public void testCategory() throws Exception {
        assertThat(action.getCategory(), is(ActionCategory.CLEANSING.getDisplayName()));
    }

    @Test
    public void should_delete() {
        Map<String, String> values = new HashMap<>();
        values.put("name", "David Bowie");
        values.put("city", "Berlin");
        DataSetRow dsr = new DataSetRow(values);

        dsr = consumer.apply(dsr, new TransformationContext());
        assertTrue(dsr.isDeleted());

        // Assert that action did not change the row values
        assertEquals("David Bowie", dsr.get("name"));
        assertEquals("Berlin", dsr.get("city"));
    }

    @Test
    public void should_delete_even_with_leading_space() {
        Map<String, String> values = new HashMap<>();
        values.put("name", "David Bowie");
        values.put("city", " Berlin"); // notice the space before ' Berlin'
        DataSetRow dsr = new DataSetRow(values);

        dsr = consumer.apply(dsr, new TransformationContext());
        assertTrue(dsr.isDeleted());

        assertEquals("David Bowie", dsr.get("name"));
        assertEquals(" Berlin", dsr.get("city"));
    }

    @Test
    public void should_delete_even_with_trailing_space() {
        Map<String, String> values = new HashMap<>();
        values.put("name", "David Bowie");
        values.put("city", "Berlin "); // notice the space after 'Berlin '
        DataSetRow dsr = new DataSetRow(values);

        dsr = consumer.apply(dsr, new TransformationContext());
        assertTrue(dsr.isDeleted());

        // Assert that action did not change the row values
        assertEquals("David Bowie", dsr.get("name"));
        assertEquals("Berlin ", dsr.get("city"));
    }

    @Test
    public void should_delete_even_with_enclosing_spaces() {
        Map<String, String> values = new HashMap<>();
        values.put("name", "David Bowie");
        values.put("city", " Berlin "); // notice the spaces enclosing ' Berlin '
        DataSetRow dsr = new DataSetRow(values);

        dsr = consumer.apply(dsr, new TransformationContext());
        assertTrue(dsr.isDeleted());

        // Assert that action did not change the row values
        assertEquals("David Bowie", dsr.get("name"));
        assertEquals(" Berlin ", dsr.get("city"));
    }

    @Test
    public void should_not_delete_because_value_not_found() {
        Map<String, String> values = new HashMap<>();
        values.put("name", "David Bowie");
        DataSetRow dsr = new DataSetRow(values);

        dsr = consumer.apply(dsr, new TransformationContext());
        assertFalse(dsr.isDeleted());

        // Assert that action did not change the row values
        assertEquals("David Bowie", dsr.get("name"));
    }

    @Test
    public void should_not_delete_because_of_case() {
        Map<String, String> values = new HashMap<>();
        values.put("name", "David Bowie");
        values.put("city", "berlin");
        DataSetRow dsr = new DataSetRow(values);

        dsr = consumer.apply(dsr, new TransformationContext());
        assertFalse(dsr.isDeleted());

        // Assert that action did not change the row values
        assertEquals("David Bowie", dsr.get("name"));
        assertEquals("berlin", dsr.get("city"));
    }

    @Test
    public void should_not_delete_because_value_different() {
        Map<String, String> values = new HashMap<>();
        values.put("name", "David Bowie");
        values.put("city", "üBerlin");
        DataSetRow dsr = new DataSetRow(values);

        dsr = consumer.apply(dsr, new TransformationContext());
        assertFalse(dsr.isDeleted());

        // Assert that action did not change the row values
        assertEquals("David Bowie", dsr.get("name"));
        assertEquals("üBerlin", dsr.get("city"));
    }

    @Test
    public void should_accept_column() {
        assertTrue(action.acceptColumn(getColumn(Type.STRING)));
        assertTrue(action.acceptColumn(getColumn(Type.NUMERIC)));
        assertTrue(action.acceptColumn(getColumn(Type.FLOAT)));
    }

    @Test
    public void should_not_accept_column() {
        assertFalse(action.acceptColumn(getColumn(Type.DATE)));
        assertFalse(action.acceptColumn(getColumn(Type.BOOLEAN)));
        assertFalse(action.acceptColumn(getColumn(Type.ANY)));
    }

}
