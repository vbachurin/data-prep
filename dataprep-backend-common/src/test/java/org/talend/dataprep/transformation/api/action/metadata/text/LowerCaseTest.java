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
 * @see LowerCase
 */
public class LowerCaseTest {

    /** The row consumer to test. */
    private DataSetRowAction closure;

    /** The action to test. */
    private LowerCase action;

    /**
     * Constructor.
     */
    public LowerCaseTest() throws IOException {

        action = new LowerCase();

        Map<String, String> parameters = ActionMetadataTestUtils.parseParameters( //
                action, //
                LowerCaseTest.class.getResourceAsStream("lowercase.json"));

        closure = action.create(parameters).getRowAction();
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
     * @see LowerCase#create(Map)
     */
    @Test
    public void should_lowercase() {
        Map<String, String> values = new HashMap<>();
        values.put("name", "Vincent");
        values.put("entity", "R&D");
        values.put("joined", "May 20th 2015");
        DataSetRow row = new DataSetRow(values);

        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("name", "Vincent");
        expectedValues.put("entity", "r&d"); // R&D --> r&d
        expectedValues.put("joined", "May 20th 2015");

        row = closure.apply(row, new TransformationContext());
        assertEquals(expectedValues, row.values());
    }

    /**
     * @see LowerCase#create(Map)
     */
    @Test
    public void should_do_nothing_since_column_does_not_exist() {
        Map<String, String> values = new HashMap<>();
        values.put("name", "Vincent");
        values.put("joined", "May 20th 2015");
        DataSetRow row = new DataSetRow(values);

        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("name", "Vincent");
        expectedValues.put("joined", "May 20th 2015");

        row = closure.apply(row, new TransformationContext());
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
