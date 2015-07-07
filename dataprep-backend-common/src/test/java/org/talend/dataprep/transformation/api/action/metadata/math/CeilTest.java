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
package org.talend.dataprep.transformation.api.action.metadata.math;

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
import org.talend.dataprep.transformation.api.action.metadata.ActionMetadataTestUtils;

/**
 * Test class for Ceil action. Creates one consumer, and test it.
 * 
 * @see Ceil
 */
public class CeilTest {

    /** The action ton test. */
    private Ceil action;

    /** The consumer out of the consumer. */
    private BiConsumer<DataSetRow, TransformationContext> consumer;

    /**
     * Constructor.
     */
    public CeilTest() throws IOException {
        action = new Ceil();
        Map<String, String> parameters = ActionMetadataTestUtils //
                .parseParameters(action, CeilTest.class.getResourceAsStream("ceilAction.json"));
        consumer = action.create(parameters).getRowAction();
    }

    public void testCommon(String input, String expected) {
        Map<String, String> values = new HashMap<>();
        values.put("aNumber", input);
        DataSetRow dsr = new DataSetRow(values);

        consumer.accept(dsr, new TransformationContext());
        assertEquals(expected, dsr.get("aNumber"));
    }

    @Test
    public void testPositive() {
        testCommon("5.0", "5");
        testCommon("5.1", "6");
        testCommon("5.5", "6");
        testCommon("5.8", "6");
    }

    @Test
    public void testNegative() {
        testCommon("-5.0", "-5");
        testCommon("-5.4", "-5");
        testCommon("-5.6", "-5");
    }

    @Test
    public void testInteger() {
        testCommon("5", "5");
        testCommon("-5", "-5");
    }

    @Test
    public void testString() {
        testCommon("tagada", "tagada");
        testCommon("", "");
        testCommon("null", "null");
    }

    @Test
    public void should_accept_column() {
        assertTrue(action.accept(getColumn(Type.NUMERIC)));
        assertTrue(action.accept(getColumn(Type.INTEGER)));
        assertTrue(action.accept(getColumn(Type.DOUBLE)));
        assertTrue(action.accept(getColumn(Type.FLOAT)));
    }

    @Test
    public void should_not_accept_column() {
        assertFalse(action.accept(getColumn(Type.STRING)));
        assertFalse(action.accept(getColumn(Type.DATE)));
        assertFalse(action.accept(getColumn(Type.BOOLEAN)));
    }
}
