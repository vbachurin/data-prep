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
import static org.talend.dataprep.api.dataset.DataSetRowWithDiff.FLAG.DELETE;
import static org.talend.dataprep.api.dataset.DataSetRowWithDiff.ROW_DIFF_KEY;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.DataSetRowWithDiff;
import org.talend.dataprep.transformation.Application;
import org.talend.dataprep.transformation.TransformationServiceTests;

/**
 * Test class for DeleteEmpty action. Creates one consumer, and test it.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@IntegrationTest
@WebAppConfiguration
public class DeleteOnValueTest {

    private Consumer<DataSetRow> consumer;

    @Autowired
    DeleteOnValue deleteOnValue;

    @Before
    public void setUp() throws IOException {
        String actions = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("deleteOnValueAction.json"));

        ObjectMapper mapper = new ObjectMapper(new JsonFactory());
        String content = actions.trim();
        JsonNode node = mapper.readTree(content);
        Map<String, String> parameters = deleteOnValue.parseParameters(node.get("actions").get(0).get("parameters").getFields());
        consumer = deleteOnValue.create(parameters);
    }

    @Test
    public void should_delete() {
        Map<String, String> values = new HashMap<>();
        values.put("name", "David Bowie");
        values.put("city", "Berlin");
        DataSetRow dsr = new DataSetRow(values);

        consumer.accept(dsr);
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

        consumer.accept(dsr);
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

        consumer.accept(dsr);
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

        consumer.accept(dsr);
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

        consumer.accept(dsr);
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

        consumer.accept(dsr);
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

        consumer.accept(dsr);
        assertFalse(dsr.isDeleted());

        // Assert that action did not change the row values
        assertEquals("David Bowie", dsr.get("name"));
        assertEquals("üBerlin", dsr.get("city"));
    }

    @Test
    public void should_delete_with_diff() {
        Map<String, String> values = new HashMap<>();
        values.put("name", "David Bowie");
        values.put("city", "Berlin");
        DataSetRowWithDiff row = new DataSetRowWithDiff(new DataSetRow(values), new DataSetRow(values));

        Map<String, String> expectedValues = new HashMap<>();
        expectedValues.putAll(values);
        expectedValues.put(ROW_DIFF_KEY, DELETE.getValue());

        consumer.accept(row);
        assertTrue(row.isDeleted());

        assertEquals(expectedValues, row.values());
    }

    @Test
    public void should_not_delete_with_diff() {
        Map<String, String> values = new HashMap<>();
        values.put("name", "David Bowie");
        values.put("city", "Aberdeen");
        DataSetRowWithDiff row = new DataSetRowWithDiff(new DataSetRow(values), new DataSetRow(values));

        consumer.accept(row);
        assertFalse(row.isDeleted());

        assertEquals(values, row.values());
    }
}
