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
 * Test class for Trim action. Creates one consumer, and test it.
 * 
 * @see Trim
 */
public class TrimTest {

    /** The action to test. */
    private Trim action;

    /** The consumer out of the action. */
    private BiConsumer<DataSetRow, TransformationContext> consumer;

    /**
     * Constructor.
     */
    public TrimTest() throws IOException {

        action = new Trim();

        Map<String, String> parameters = ActionMetadataTestUtils.parseParameters( //
                action, //
                TrimTest.class.getResourceAsStream("trimAction.json"));

        consumer = action.create(parameters);
    }

    @Test
    public void testAdapt() throws Exception {
        assertThat(action.adapt(null), is(action));
        ColumnMetadata column = column().name("myColumn").id(0).type(Type.STRING).build();
        assertThat(action.adapt(column), is(action));
    }

    @Test
    public void test1() {
        Map<String, String> values = new HashMap<>();
        values.put("band", " the beatles ");
        DataSetRow dsr = new DataSetRow(values);

        consumer.accept(dsr, new TransformationContext());

        assertEquals("the beatles", dsr.get("band"));
    }

    @Test
    public void test2() {
        Map<String, String> values = new HashMap<>();
        values.put("band", "The  Beatles");
        DataSetRow dsr = new DataSetRow(values);

        consumer.accept(dsr, new TransformationContext());

        assertEquals("The  Beatles", dsr.get("band"));
    }

    @Test
    public void test3() {
        Map<String, String> values = new HashMap<>();
        values.put("bando", "the beatles");
        DataSetRow dsr = new DataSetRow(values);

        consumer.accept(dsr, new TransformationContext());

        assertEquals("the beatles", dsr.get("bando"));
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
