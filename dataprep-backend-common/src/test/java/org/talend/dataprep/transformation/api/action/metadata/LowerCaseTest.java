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
package org.talend.dataprep.transformation.api.action.metadata;

import static org.junit.Assert.*;
import static org.talend.dataprep.transformation.api.action.metadata.ActionMetadataTestUtils.getColumn;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import org.junit.Test;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;

/**
 * Test class for LowerCase action. Creates one consumer, and test it.
 * 
 * @see LowerCase
 */
public class LowerCaseTest {

    /** The row consumer to test. */
    private BiConsumer<DataSetRow, TransformationContext> closure;

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

        closure.accept(row, new TransformationContext());
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

        closure.accept(row, new TransformationContext());
        assertEquals(expectedValues, row.values());
    }

    @Test
    public void should_accept_column() {
        assertTrue(action.accept(getColumn(Type.STRING)));
    }

    @Test
    public void should_not_accept_column() {
        assertFalse(action.accept(getColumn(Type.NUMERIC)));
        assertFalse(action.accept(getColumn(Type.FLOAT)));
        assertFalse(action.accept(getColumn(Type.DATE)));
        assertFalse(action.accept(getColumn(Type.BOOLEAN)));
    }
}
