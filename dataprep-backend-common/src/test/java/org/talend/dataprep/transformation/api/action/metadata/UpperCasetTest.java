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
import java.util.function.Consumer;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.type.Type;

/**
 * Test class for LowerCase action. Creates one consumer, and test it.
 * 
 * @see UpperCase
 */

public class UpperCasetTest {

    /** The action to test. */
    private UpperCase action;

    /** The row consumer to test. */
    private Consumer<DataSetRow> rowClosure;

    /**
     * Constructor.
     */
    public UpperCasetTest() throws IOException {
        action = new UpperCase();

        String actions = IOUtils.toString(UpperCasetTest.class.getResourceAsStream("uppercase.json"));
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
