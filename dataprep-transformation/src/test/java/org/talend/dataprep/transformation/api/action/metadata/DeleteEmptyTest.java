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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.BeforeClass;
import org.junit.Test;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.transformation.TransformationServiceTests;

/**
 * Test class for DeleteEmpty action. Creates one consumer, and test it.
 */
public class DeleteEmptyTest {

    private static Consumer<DataSetRow> consumer;

    @BeforeClass
    public static void setUpClass() throws IOException {
        String actions = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("deleteEmptyAction.json"));

        ObjectMapper mapper = new ObjectMapper(new JsonFactory());
        String content = actions.trim();
        JsonNode node = mapper.readTree(content);

        consumer = new DeleteEmpty().create(node.get("actions").get(0).get("parameters").getFields());
    }

    @Test
    public void testDelete1() {
        Map<String, String> values = new HashMap<>();
        values.put("name", "David Bowie");
        DataSetRow dsr = new DataSetRow(values);

        consumer.accept(dsr);
        assertTrue(dsr.isDeleted());

        // Assert that action did not change the row values
        assertEquals("David Bowie", dsr.get("name"));
    }

    @Test
    public void testDelete2() {
        Map<String, String> values = new HashMap<>();
        values.put("name", "David Bowie");
        values.put("city", null);
        DataSetRow dsr = new DataSetRow(values);

        consumer.accept(dsr);
        assertTrue(dsr.isDeleted());
    }

    @Test
    public void testDelete3() {
        Map<String, String> values = new HashMap<>();
        values.put("name", "David Bowie");
        values.put("city", "");
        DataSetRow dsr = new DataSetRow(values);

        consumer.accept(dsr);
        assertTrue(dsr.isDeleted());
    }

    @Test
    public void testDelete4() {
        Map<String, String> values = new HashMap<>();
        values.put("name", "David Bowie");
        values.put("city", " ");
        DataSetRow dsr = new DataSetRow(values);

        consumer.accept(dsr);
        assertTrue(dsr.isDeleted());
    }

    @Test
    public void testNotDelete1() {
        Map<String, String> values = new HashMap<>();
        values.put("name", "David Bowie");
        values.put("city", "-");
        DataSetRow dsr = new DataSetRow(values);

        consumer.accept(dsr);
        assertFalse(dsr.isDeleted());

        // Assert that action did not change the row values
        assertEquals("David Bowie", dsr.get("name"));
        assertEquals("-", dsr.get("city"));
    }

    @Test
    public void testNotDelete2() {
        Map<String, String> values = new HashMap<>();
        values.put("name", "David Bowie");
        values.put("city", " a value ");
        DataSetRow dsr = new DataSetRow(values);

        consumer.accept(dsr);
        assertFalse(dsr.isDeleted());
    }

    @Test
    public void testNotDelete3() {
        Map<String, String> values = new HashMap<>();
        values.put("name", "David Bowie");
        values.put("city", "true");
        DataSetRow dsr = new DataSetRow(values);

        consumer.accept(dsr);
        assertFalse(dsr.isDeleted());
    }

    @Test
    public void testNotDelete4() {
        Map<String, String> values = new HashMap<>();
        values.put("name", "David Bowie");
        values.put("city", "45");
        DataSetRow dsr = new DataSetRow(values);

        consumer.accept(dsr);
        assertFalse(dsr.isDeleted());
    }

    @Test
    public void testNotDelete5() {
        Map<String, String> values = new HashMap<>();
        values.put("name", "David Bowie");
        values.put("city", "-12");
        DataSetRow dsr = new DataSetRow(values);

        consumer.accept(dsr);
        assertFalse(dsr.isDeleted());
    }

    @Test
    public void testNotDelete6() {
        Map<String, String> values = new HashMap<>();
        values.put("name", "David Bowie");
        values.put("city", "0.001");
        DataSetRow dsr = new DataSetRow(values);

        consumer.accept(dsr);
        assertFalse(dsr.isDeleted());
    }
}
