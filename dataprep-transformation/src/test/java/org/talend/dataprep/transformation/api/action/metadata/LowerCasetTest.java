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

import static org.junit.Assert.assertEquals;

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
 * Test class for LowerCase action. Creates one consumer, and test it.
 * 
 * @see LowerCase
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@IntegrationTest
@WebAppConfiguration
public class LowerCasetTest {

    /** The row consumer to test. */
    private Consumer<DataSetRow> rowClosure;

    /** The action to test. */
    @Autowired
    private LowerCase action;

    /**
     * Initialization before each test.
     */
    @Before
    public void setUp() throws IOException {
        String actions = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("lowercase.json"));
        ObjectMapper mapper = new ObjectMapper(new JsonFactory());
        String content = actions.trim();
        JsonNode node = mapper.readTree(content);
        Map<String, String> parameters = action.parseParameters(node.get("actions").get(0).get("parameters").getFields());
        rowClosure = action.create(parameters);
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

        rowClosure.accept(row);
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

        rowClosure.accept(row);
        assertEquals(expectedValues, row.values());
    }

}
