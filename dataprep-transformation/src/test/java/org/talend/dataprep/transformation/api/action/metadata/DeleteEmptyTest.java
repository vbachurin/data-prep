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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.transformation.Application;
import org.talend.dataprep.transformation.TransformationServiceTests;

/**
 * Test class for DeleteEmpty action. Creates one consumer, and test it.
 *
 * @see DeleteEmpty
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@IntegrationTest
@WebAppConfiguration
public class DeleteEmptyTest {

    private Consumer<DataSetRow> consumer;

    /** The action to test. */
    @Autowired
    private DeleteEmpty deleteEmpty;

    @Before
    public void setUp() throws IOException {
        String actions = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("deleteEmptyAction.json"));
        ObjectMapper mapper = new ObjectMapper(new JsonFactory());
        String content = actions.trim();
        JsonNode node = mapper.readTree(content);
        Map<String, String> parameters = deleteEmpty.parseParameters(node.get("actions").get(0).get("parameters").getFields());
        consumer = deleteEmpty.create(parameters);
    }

    @Test
    public void should_delete_because_value_not_set() {
        Map<String, String> values = new HashMap<>();
        values.put("name", "David Bowie");
        DataSetRow dsr = new DataSetRow(values);

        consumer.accept(dsr);
        assertTrue(dsr.isDeleted());

        // Assert that action did not change the row values
        assertEquals("David Bowie", dsr.get("name"));
    }

    @Test
    public void should_delete_because_null() {
        Map<String, String> values = new HashMap<>();
        values.put("name", "David Bowie");
        values.put("city", null);
        DataSetRow dsr = new DataSetRow(values);

        consumer.accept(dsr);
        assertTrue(dsr.isDeleted());
    }

    @Test
    public void should_delete_because_empty() {
        Map<String, String> values = new HashMap<>();
        values.put("name", "David Bowie");
        values.put("city", "");
        DataSetRow dsr = new DataSetRow(values);

        consumer.accept(dsr);
        assertTrue(dsr.isDeleted());
    }

    @Test
    public void should_delete_because_value_is_made_of_spaces() {
        Map<String, String> values = new HashMap<>();
        values.put("name", "David Bowie");
        values.put("city", " ");
        DataSetRow dsr = new DataSetRow(values);

        consumer.accept(dsr);
        assertTrue(dsr.isDeleted());
    }

    @Test
    public void should_not_delete_because_value_set() {
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
    public void should_not_delete_because_value_set_2() {
        Map<String, String> values = new HashMap<>();
        values.put("name", "David Bowie");
        values.put("city", " a value ");
        DataSetRow dsr = new DataSetRow(values);

        consumer.accept(dsr);
        assertFalse(dsr.isDeleted());
    }

    @Test
    public void should_not_delete_because_value_set_of_boolean() {
        Map<String, String> values = new HashMap<>();
        values.put("name", "David Bowie");
        values.put("city", "true");
        DataSetRow dsr = new DataSetRow(values);

        consumer.accept(dsr);
        assertFalse(dsr.isDeleted());
    }

    @Test
    public void should_not_delete_because_value_set_of_number() {
        Map<String, String> values = new HashMap<>();
        values.put("name", "David Bowie");
        values.put("city", "45");
        DataSetRow dsr = new DataSetRow(values);

        consumer.accept(dsr);
        assertFalse(dsr.isDeleted());
    }

    @Test
    public void should_not_delete_because_value_set_of_negative_boolean() {
        Map<String, String> values = new HashMap<>();
        values.put("name", "David Bowie");
        values.put("city", "-12");
        DataSetRow dsr = new DataSetRow(values);

        consumer.accept(dsr);
        assertFalse(dsr.isDeleted());
    }

    @Test
    public void should_not_delete_because_value_set_of_float() {
        Map<String, String> values = new HashMap<>();
        values.put("name", "David Bowie");
        values.put("city", "0.001");
        DataSetRow dsr = new DataSetRow(values);

        consumer.accept(dsr);
        assertFalse(dsr.isDeleted());
    }

}
