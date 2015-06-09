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

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
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

        String actions = IOUtils.toString(TrimTest.class.getResourceAsStream("trimAction.json"));
        ObjectMapper mapper = new ObjectMapper(new JsonFactory());
        String content = actions.trim();
        JsonNode node = mapper.readTree(content);
        Map<String, String> parameters = action.parseParameters(node.get("actions").get(0).get("parameters").getFields());
        consumer = action.create(parameters);
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
