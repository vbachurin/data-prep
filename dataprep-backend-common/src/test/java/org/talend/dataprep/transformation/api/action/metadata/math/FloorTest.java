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

/**
 * Test class for Floor action. Creates one consumer, and test it.
 *
 * @see Floor
 */
public class FloorTest {

    /** The action ton test. */
    private Floor action;

    /** The consumer out of the consumer. */
    private DataSetRowAction consumer;

    /**
     * Constructor.
     */
    public FloorTest() throws IOException {
        action = new Floor();
        Map<String, String> parameters = ActionMetadataTestUtils //
                .parseParameters(action, FloorTest.class.getResourceAsStream("floorAction.json"));
        consumer = action.create(parameters).getRowAction();
    }

    @Test
    public void testAdapt() throws Exception {
        assertThat(action.adapt(null), is(action));
        ColumnMetadata column = column().name("myColumn").id(0).type(Type.STRING).build();
        assertThat(action.adapt(column), is(action));
    }

    public void testCommon(String input, String expected) {
        Map<String, String> values = new HashMap<>();
        values.put("aNumber", input);
        DataSetRow dsr = new DataSetRow(values);

        dsr = consumer.apply(dsr, new TransformationContext());
        assertEquals(expected, dsr.get("aNumber"));
    }

    @Test
    public void testPositive() {
        testCommon("5.0", "5");
        testCommon("5.1", "5");
        testCommon("5.5", "5");
        testCommon("5.8", "5");
    }

    @Test
    public void testNegative() {
        testCommon("-5.0", "-5");
        testCommon("-5.4", "-6");
        testCommon("-5.6", "-6");
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
        assertTrue(action.acceptColumn(getColumn(Type.NUMERIC)));
        assertTrue(action.acceptColumn(getColumn(Type.INTEGER)));
        assertTrue(action.acceptColumn(getColumn(Type.DOUBLE)));
        assertTrue(action.acceptColumn(getColumn(Type.FLOAT)));
    }

    @Test
    public void should_not_accept_column() {
        assertFalse(action.acceptColumn(getColumn(Type.STRING)));
        assertFalse(action.acceptColumn(getColumn(Type.DATE)));
        assertFalse(action.acceptColumn(getColumn(Type.BOOLEAN)));
    }
}
