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
import static org.talend.dataprep.api.dataset.DataSetRowWithDiff.DIFF_KEY;
import static org.talend.dataprep.api.dataset.DataSetRowWithDiff.FLAG.UPDATE;

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
 * Test class for LowerCase action. Creates one consumer, and test it.
 * 
 * @see UpperCase
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@IntegrationTest
@WebAppConfiguration
public class UpperCasetTest {

    /** The row consumer to test. */
    private Consumer<DataSetRow> rowClosure;

    /** The action to test. */
    @Autowired
    private UpperCase action;

    /**
     * Initialization before each test.
     */
    @Before
    public void setUp() throws IOException {
        String actions = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("uppercase.json"));
        ObjectMapper mapper = new ObjectMapper(new JsonFactory());
        String content = actions.trim();
        JsonNode node = mapper.readTree(content);
        Map<String, String> parameters = action.parseParameters(node.get("actions").get(0).get("parameters").getFields());
        rowClosure = action.create(parameters);
    }

    /**
     * @see UpperCase#create(Map)
     */
    @Test
    public void should_uppercase() {
        Map<String, String> values = new HashMap<>();
        values.put("city", "Vancouver");
        values.put("country", "Canada");
        DataSetRow row = new DataSetRow(values);

        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("city", "VANCOUVER"); // Vancouver --> VANCOUVER
        expectedValues.put("country", "Canada");

        rowClosure.accept(row);
        assertEquals(expectedValues, row.values());
    }

    /**
     * @see UpperCase#create(Map)
     */
    @Test()
    public void should_do_nothing_since_column_does_not_exist() {
        Map<String, String> values = new HashMap<>();
        values.put("country", "Canada");
        values.put("capital", "Ottawa");
        DataSetRow row = new DataSetRow(values);

        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("country", "Canada");
        expectedValues.put("capital", "Ottawa");

        rowClosure.accept(row);
        assertEquals(expectedValues, row.values());
    }

    /**
     * @see UpperCase#create(Map)
     */
    @Test
    public void should_uppercase_with_diff() {
        Map<String, String> values = new HashMap<>();
        values.put("city", "Vancouver");
        values.put("country", "Canada");
        DataSetRowWithDiff row = new DataSetRowWithDiff(new DataSetRow(values), new DataSetRow(values));

        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("city", "VANCOUVER"); // Vancouver --> VANCOUVER
        expectedValues.put("country", "Canada");

        // compute diff flag
        Map<String, Object> diff = new HashMap<>();
        diff.put("city", UPDATE.getValue());
        expectedValues.put(DIFF_KEY, diff);

        rowClosure.accept(row);
        assertEquals(expectedValues, row.values());
    }

    /**
     * @see UpperCase#create(Map)
     */
    @Test
    public void should_uppercase_without_diff() {
        Map<String, String> values = new HashMap<>();
        values.put("city", "VANCOUVER");
        values.put("country", "Canada");
        DataSetRowWithDiff row = new DataSetRowWithDiff(new DataSetRow(values), new DataSetRow(values));

        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("city", "VANCOUVER"); // Vancouver --> VANCOUVER
        expectedValues.put("country", "Canada");

        rowClosure.accept(row);
        assertEquals(expectedValues, row.values());
    }
}
