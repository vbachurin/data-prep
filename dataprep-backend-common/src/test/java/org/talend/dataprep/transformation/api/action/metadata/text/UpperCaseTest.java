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
 * Test class for LowerCase action. Creates one consumer, and test it.
 *
 * @see UpperCase
 */

public class UpperCaseTest {

    /** The action to test. */
    private UpperCase action;

    /** The row consumer to test. */
    private DataSetRowAction rowClosure;

    /**
     * Constructor.
     */
    public UpperCaseTest() throws IOException {
        action = new UpperCase();

        Map<String, String> parameters = ActionMetadataTestUtils.parseParameters( //
                action, //
                UpperCaseTest.class.getResourceAsStream("uppercase.json"));

        rowClosure = action.create(parameters).getRowAction();
    }

    @Test
    public void testAdapt() throws Exception {
        assertThat(action.adapt(null), is(action));
        ColumnMetadata column = column().name("myColumn").id(0).type(Type.STRING).build();
        assertThat(action.adapt(column), is(action));
    }

    @Test
    public void testCategory() throws Exception {
        assertThat(action.getCategory(), is(ActionCategory.CASE.getDisplayName()));
    }

    /**
     * @see UpperCase#create(Map)
     */
    @Test
    public void should_uppercase() {
        Map<String, String> values = new HashMap<>();
        values.put("city", "Vancouver");
        values.put("country", "Canada");
        DataSetRow row = new DataSetRow(values);

        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("city", "VANCOUVER"); // Vancouver --> VANCOUVER
        expectedValues.put("country", "Canada");

        row = rowClosure.apply(row, new TransformationContext());
        assertEquals(expectedValues, row.values());
    }

    /**
     * @see UpperCase#create(Map)
     */
    @Test()
    public void should_do_nothing_since_column_does_not_exist() {
        Map<String, String> values = new HashMap<>();
        values.put("country", "Canada");
        values.put("capital", "Ottawa");
        DataSetRow row = new DataSetRow(values);

        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("country", "Canada");
        expectedValues.put("capital", "Ottawa");

        row = rowClosure.apply(row, new TransformationContext());
        assertEquals(expectedValues, row.values());
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

}
