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
package org.talend.dataprep.transformation.api.action.metadata.text;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.talend.dataprep.api.dataset.ColumnMetadata.Builder.column;
import static org.talend.dataprep.transformation.api.action.metadata.ActionMetadataTestUtils.getColumn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.api.preparation.Action;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.DataSetRowAction;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;
import org.talend.dataprep.transformation.api.action.metadata.ActionMetadataTestUtils;
import org.talend.dataprep.transformation.api.action.metadata.category.ActionCategory;

/**
 * Test class for Split action. Creates one consumer, and test it.
 *
 * @see Split
 */
public class SubstringTest {

    /** The row consumer to test. */
    private DataSetRowAction rowClosure;

    /** The action to test. */
    private Substring action;

    /**
     * Constructor.
     */
    public SubstringTest() throws IOException {
        action = new Substring();
        Map<String, String> parameters = ActionMetadataTestUtils.parseParameters( //
                action, //
                SubstringTest.class.getResourceAsStream("substringAction.json"));
        final Action action = this.action.create(parameters);
        rowClosure = action.getRowAction();
    }

    @Test
    public void testAdapt() throws Exception {
        assertThat(action.adapt(null), is(action));
        ColumnMetadata column = column().name("myColumn").id(0).type(Type.STRING).build();
        assertThat(action.adapt(column), is(action));
    }

    @Test
    public void testCategory() throws Exception {
        assertThat(action.getCategory(), is(ActionCategory.QUICKFIX.getDisplayName()));
    }

    /**
     * @see Split#create(Map)
     */
    @Test
    public void should_substring() {
        Map<String, String> values = new HashMap<>();
        values.put("0000", "lorem bacon");
        values.put("0001", "Bacon ipsum dolor amet swine leberkas pork belly");
        values.put("0002", "01/01/2015");
        DataSetRow row = new DataSetRow(values);

        Map<String, String> expectedValues = new HashMap<>();
        expectedValues.put("0000", "lorem bacon");
        expectedValues.put("0001", "Bacon ipsum dolor amet swine leberkas pork belly");
        expectedValues.put("0003", " ipsum ");
        expectedValues.put("0002", "01/01/2015");

        row = rowClosure.apply(row, new TransformationContext());
        assertEquals(expectedValues, row.values());
    }

    /**
     * @see Split#create(Map)
     */
    @Test
    public void should_substring_out_of_bound_1() {
        Map<String, String> values = new HashMap<>();
        values.put("0000", "lorem bacon");
        values.put("0001", "Bacon ip");
        values.put("0002", "01/01/2015");
        DataSetRow row = new DataSetRow(values);

        Map<String, String> expectedValues = new HashMap<>();
        expectedValues.put("0000", "lorem bacon");
        expectedValues.put("0001", "Bacon ip");
        expectedValues.put("0003", " ip");
        expectedValues.put("0002", "01/01/2015");

        row = rowClosure.apply(row, new TransformationContext());
        assertEquals(expectedValues, row.values());
    }

    /**
     * @see Split#create(Map)
     */
    @Test
    public void should_substring_out_of_bound_2() {
        Map<String, String> values = new HashMap<>();
        values.put("0000", "lorem bacon");
        values.put("0001", "Bac");
        values.put("0002", "01/01/2015");
        DataSetRow row = new DataSetRow(values);

        Map<String, String> expectedValues = new HashMap<>();
        expectedValues.put("0000", "lorem bacon");
        expectedValues.put("0001", "Bac");
        expectedValues.put("0003", "");
        expectedValues.put("0002", "01/01/2015");

        row = rowClosure.apply(row, new TransformationContext());
        assertEquals(expectedValues, row.values());
    }

    /**
     * @see Split#create(Map)
     */
    @Test
    public void should_substring_out_of_bound_3() {
        Map<String, String> values = new HashMap<>();
        values.put("0000", "lorem bacon");
        values.put("0001", "");
        values.put("0002", "01/01/2015");
        DataSetRow row = new DataSetRow(values);

        Map<String, String> expectedValues = new HashMap<>();
        expectedValues.put("0000", "lorem bacon");
        expectedValues.put("0001", "");
        expectedValues.put("0003", "");
        expectedValues.put("0002", "01/01/2015");

        row = rowClosure.apply(row, new TransformationContext());
        assertEquals(expectedValues, row.values());
    }

    /**
     * @throws IOException
     * @see Split#create(Map)
     */
    @Test
    public void should_substring_strange_bounds_1() throws IOException {
        Map<String, String> values = new HashMap<>();
        values.put("0000", "lorem bacon");
        values.put("0001", "Bacon ipsum dolor amet swine leberkas pork belly");
        values.put("0002", "01/01/2015");
        DataSetRow row = new DataSetRow(values);

        Map<String, String> expectedValues = new HashMap<>();
        expectedValues.put("0000", "lorem bacon");
        expectedValues.put("0001", "Bacon ipsum dolor amet swine leberkas pork belly");
        expectedValues.put("0003", "");
        expectedValues.put("0002", "01/01/2015");

        // =====================================================
        // Create a new rowClosure with different params:
        // =====================================================
        Map<String, String> parameters = ActionMetadataTestUtils.parseParameters( //
                action, //
                SubstringTest.class.getResourceAsStream("substringAction.json"));

        parameters.put(Substring.FROM_INDEX_PARAMETER, "6");
        parameters.put(Substring.TO_INDEX_PARAMETER, "1");

        Action alternativeAction = this.action.create(parameters);
        DataSetRowAction alternativeRowClosure = alternativeAction.getRowAction();
        // =====================================================

        row = alternativeRowClosure.apply(row, new TransformationContext());
        assertEquals(expectedValues, row.values());
    }

    /**
     * @throws IOException
     * @see Split#create(Map)
     */
    @Test
    public void should_substring_strange_bounds_2() throws IOException {
        Map<String, String> values = new HashMap<>();
        values.put("0000", "lorem bacon");
        values.put("0001", "Bacon ipsum dolor amet swine leberkas pork belly");
        values.put("0002", "01/01/2015");
        DataSetRow row = new DataSetRow(values);

        Map<String, String> expectedValues = new HashMap<>();
        expectedValues.put("0000", "lorem bacon");
        expectedValues.put("0001", "Bacon ipsum dolor amet swine leberkas pork belly");
        expectedValues.put("0003", "psum dolor amet swine leberkas pork belly");
        expectedValues.put("0002", "01/01/2015");

        // =====================================================
        // Create a new rowClosure with different params:
        // =====================================================
        Map<String, String> parameters = ActionMetadataTestUtils.parseParameters( //
                action, //
                SubstringTest.class.getResourceAsStream("substringAction.json"));

        parameters.put(Substring.FROM_INDEX_PARAMETER, "7");
        parameters.put(Substring.TO_MODE_PARAMETER, "To end");
        parameters.put(Substring.TO_INDEX_PARAMETER, "");

        Action alternativeAction = this.action.create(parameters);
        DataSetRowAction alternativeRowClosure = alternativeAction.getRowAction();
        // =====================================================

        row = alternativeRowClosure.apply(row, new TransformationContext());
        assertEquals(expectedValues, row.values());
    }

    /**
     * @throws IOException
     * @see Split#create(Map)
     */
    @Test
    public void should_substring_twice() throws IOException {
        Map<String, String> values = new HashMap<>();
        values.put("0000", "lorem bacon");
        values.put("0001", "Bacon ipsum dolor amet swine leberkas pork belly");
        values.put("0002", "01/01/2015");
        DataSetRow row = new DataSetRow(values);

        Map<String, String> expectedValues = new HashMap<>();
        expectedValues.put("0000", "lorem bacon");
        expectedValues.put("0001", "Bacon ipsum dolor amet swine leberkas pork belly");
        expectedValues.put("0004", "acon ");
        expectedValues.put("0003", " ipsum ");
        expectedValues.put("0002", "01/01/2015");

        row = rowClosure.apply(row, new TransformationContext());

        // =====================================================
        // Create a new rowClosure with different params:
        // =====================================================
        Map<String, String> parameters = ActionMetadataTestUtils.parseParameters( //
                action, //
                SubstringTest.class.getResourceAsStream("substringAction.json"));

        parameters.put(Substring.FROM_INDEX_PARAMETER, "1");
        parameters.put(Substring.TO_INDEX_PARAMETER, "6");

        Action alternativeAction = this.action.create(parameters);
        DataSetRowAction alternativeRowClosure = alternativeAction.getRowAction();
        // =====================================================

        row = alternativeRowClosure.apply(row, new TransformationContext());
        assertEquals(expectedValues, row.values());
    }

    /**
     * @throws IOException
     * @see Split#create(Map)
     */
    @Test
    public void should_substring_begining() throws IOException {
        Map<String, String> values = new HashMap<>();
        values.put("0000", "lorem bacon");
        values.put("0001", "Bacon ipsum dolor amet swine leberkas pork belly");
        values.put("0002", "01/01/2015");
        DataSetRow row = new DataSetRow(values);

        Map<String, String> expectedValues = new HashMap<>();
        expectedValues.put("0000", "lorem bacon");
        expectedValues.put("0001", "Bacon ipsum dolor amet swine leberkas pork belly");
        expectedValues.put("0003", "Bacon ipsum ");
        expectedValues.put("0002", "01/01/2015");

        // =====================================================
        // Create a new rowClosure with different params:
        // =====================================================
        Map<String, String> parameters = ActionMetadataTestUtils.parseParameters( //
                action, //
                SubstringTest.class.getResourceAsStream("substringAction.json"));

        parameters.put(Substring.FROM_MODE_PARAMETER, "From beginning");

        Action alternativeAction = this.action.create(parameters);
        DataSetRowAction alternativeRowClosure = alternativeAction.getRowAction();
        // =====================================================

        row = alternativeRowClosure.apply(row, new TransformationContext());
        assertEquals(expectedValues, row.values());
    }

    /**
     * @throws IOException
     * @see Split#create(Map)
     */
    @Test
    public void should_substring_end() throws IOException {
        Map<String, String> values = new HashMap<>();
        values.put("0000", "lorem bacon");
        values.put("0001", "Bacon ipsum dolor amet swine leberkas pork belly");
        values.put("0002", "01/01/2015");
        DataSetRow row = new DataSetRow(values);

        Map<String, String> expectedValues = new HashMap<>();
        expectedValues.put("0000", "lorem bacon");
        expectedValues.put("0001", "Bacon ipsum dolor amet swine leberkas pork belly");
        expectedValues.put("0003", " ipsum dolor amet swine leberkas pork belly");
        expectedValues.put("0002", "01/01/2015");

        // =====================================================
        // Create a new rowClosure with different params:
        // =====================================================
        Map<String, String> parameters = ActionMetadataTestUtils.parseParameters( //
                action, //
                SubstringTest.class.getResourceAsStream("substringAction.json"));

        parameters.put(Substring.TO_MODE_PARAMETER, "To end");

        Action alternativeAction = this.action.create(parameters);
        DataSetRowAction alternativeRowClosure = alternativeAction.getRowAction();
        // =====================================================

        row = alternativeRowClosure.apply(row, new TransformationContext());
        assertEquals(expectedValues, row.values());
    }

    /**
     * @throws IOException
     * @see Split#create(Map)
     */
    @Test
    public void should_substring_the_new_substring() throws IOException {
        Map<String, String> values = new HashMap<>();
        values.put("0000", "lorem bacon");
        values.put("0001", "Bacon ipsum dolor amet swine leberkas pork belly");
        values.put("0002", "01/01/2015");
        DataSetRow row = new DataSetRow(values);

        Map<String, String> expectedValues = new HashMap<>();
        expectedValues.put("0000", "lorem bacon");
        expectedValues.put("0001", "Bacon ipsum dolor amet swine leberkas pork belly");
        expectedValues.put("0003", " ipsum ");
        expectedValues.put("0004", "ips");
        expectedValues.put("0002", "01/01/2015");

        row = rowClosure.apply(row, new TransformationContext());

        // =====================================================
        // Create a new rowClosure with different params:
        // =====================================================
        Map<String, String> parameters = ActionMetadataTestUtils.parseParameters( //
                action, //
                SubstringTest.class.getResourceAsStream("substringAction.json"));

        parameters.put("column_id", "0003"); // Substring from newly created column
        parameters.put(Substring.FROM_INDEX_PARAMETER, "1");
        parameters.put(Substring.TO_INDEX_PARAMETER, "4");

        Action alternativeAction = this.action.create(parameters);
        DataSetRowAction alternativeRowClosure = alternativeAction.getRowAction();
        // =====================================================

        row = alternativeRowClosure.apply(row, new TransformationContext());
        assertEquals(expectedValues, row.values());
    }

    @Test
    public void should_update_metadata() {

        List<ColumnMetadata> input = new ArrayList<>();
        input.add(createMetadata("0000", "recipe"));
        input.add(createMetadata("0001", "steps"));
        input.add(createMetadata("0002", "last update"));
        RowMetadata rowMetadata = new RowMetadata(input);

        rowClosure.apply(new DataSetRow(rowMetadata), new TransformationContext());
        List<ColumnMetadata> actual = rowMetadata.getColumns();

        List<ColumnMetadata> expected = new ArrayList<>();
        expected.add(createMetadata("0000", "recipe"));
        expected.add(createMetadata("0001", "steps"));
        expected.add(createMetadata("0003", "steps_substring"));
        expected.add(createMetadata("0002", "last update"));

        assertEquals(expected, actual);
    }

    @Test
    public void should_update_metadata_twice() {

        List<ColumnMetadata> input = new ArrayList<>();
        input.add(createMetadata("0000", "recipe"));
        input.add(createMetadata("0001", "steps"));
        input.add(createMetadata("0002", "last update"));
        RowMetadata rowMetadata = new RowMetadata(input);

        DataSetRow row = new DataSetRow(rowMetadata);
        row = rowClosure.apply(row, new TransformationContext());
        row = rowClosure.apply(row, new TransformationContext());

        List<ColumnMetadata> actual = row.getRowMetadata().getColumns();

        List<ColumnMetadata> expected = new ArrayList<>();
        expected.add(createMetadata("0000", "recipe"));
        expected.add(createMetadata("0001", "steps"));
        expected.add(createMetadata("0004", "steps_substring"));
        expected.add(createMetadata("0003", "steps_substring"));
        expected.add(createMetadata("0002", "last update"));

        assertEquals(expected, actual);
    }

    @Test
    public void should_accept_column() {
        assertTrue(action.acceptColumn(getColumn(Type.STRING)));
    }

    @Test
    public void should_not_accept_column() {
        assertFalse(action.acceptColumn(getColumn(Type.NUMERIC)));
        assertFalse(action.acceptColumn(getColumn(Type.FLOAT)));
        assertFalse(action.acceptColumn(getColumn(Type.DATE)));
        assertFalse(action.acceptColumn(getColumn(Type.BOOLEAN)));
    }


    /**
     * @param name name of the column metadata to create.
     * @return a new column metadata
     */
    private ColumnMetadata createMetadata(String id, String name) {
        return ColumnMetadata.Builder.column().computedId(id).name(name).type(Type.STRING).headerSize(12).empty(0).invalid(2)
                .valid(5).build();
    }

}
