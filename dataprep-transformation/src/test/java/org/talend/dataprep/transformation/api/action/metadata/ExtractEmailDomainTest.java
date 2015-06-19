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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.Application;
import org.talend.dataprep.transformation.TransformationServiceTests;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;

/**
 * Test class for Split action. Creates one consumer, and test it.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@IntegrationTest
@WebAppConfiguration
@DirtiesContext
public class ExtractEmailDomainTest {

    /** The row consumer to test. */
    private BiConsumer<DataSetRow, TransformationContext> rowClosure;

    /** The metadata consumer to test. */
    private BiConsumer<RowMetadata, TransformationContext> metadataClosure;

    /** The action to test. */
    @Autowired
    private ExtractEmailDomain action;

    /**
     * Initialization before each test.
     */
    @Before
    public void setUp() throws IOException {
        String actions = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("extractDomainAction.json"));
        ObjectMapper mapper = new ObjectMapper(new JsonFactory());
        String content = actions.trim();
        JsonNode node = mapper.readTree(content);
        Map<String, String> parameters = action.parseParameters(node.get("actions").get(0).get("parameters").getFields());
        rowClosure = action.create(parameters);
        metadataClosure = action.createMetadataClosure(parameters);
    }

    /**
     * @see Split#create(Map)
     */
    @Test
    public void test_values() {
        Map<String, String> values = new HashMap<>();
        values.put("recipe", "lorem bacon");
        values.put("email", "david.bowie@yopmail.com");
        values.put("last update", "01/01/2015");
        DataSetRow row = new DataSetRow(values);

        Map<String, String> expectedValues = new HashMap<>();
        expectedValues.put("recipe", "lorem bacon");
        expectedValues.put("email", "david.bowie@yopmail.com");
        expectedValues.put("email_local", "david.bowie");
        expectedValues.put("email_domain", "yopmail.com");
        expectedValues.put("last update", "01/01/2015");

        rowClosure.accept(row, new TransformationContext());
        assertEquals(expectedValues, row.values());
    }

    @Test
    public void test_values_invalid() {
        Map<String, String> values = new HashMap<>();
        values.put("recipe", "lorem bacon");
        values.put("email", "david.bowie");
        values.put("last update", "01/01/2015");
        DataSetRow row = new DataSetRow(values);

        Map<String, String> expectedValues = new HashMap<>();
        expectedValues.put("recipe", "lorem bacon");
        expectedValues.put("email", "david.bowie");
        expectedValues.put("email_local", "");
        expectedValues.put("email_domain", "");
        expectedValues.put("last update", "01/01/2015");

        rowClosure.accept(row, new TransformationContext());
        assertEquals(expectedValues, row.values());
    }

    /**
     * @see Split#createMetadataClosure(Map)
     */
    @Test
    public void test_metadata() {
        List<ColumnMetadata> input = new ArrayList<>();
        input.add(createMetadata("recipe", "recipe"));
        input.add(createMetadata("email", "email"));
        input.add(createMetadata("last update", "last update"));
        RowMetadata rowMetadata = new RowMetadata(input);

        metadataClosure.accept(rowMetadata, new TransformationContext());
        List<ColumnMetadata> actual = rowMetadata.getColumns();

        List<ColumnMetadata> expected = new ArrayList<>();
        expected.add(createMetadata("recipe", "recipe"));
        expected.add(createMetadata("email", "email"));
        expected.add(createMetadata("email_local", "email_local"));
        expected.add(createMetadata("email_domain", "email_domain"));
        expected.add(createMetadata("last update", "last update"));

        assertEquals(expected, actual);
    }

    /**
     * @param name name of the column metadata to create.
     * @return a new column metadata
     */
    private ColumnMetadata createMetadata(String id, String name) {
        return ColumnMetadata.Builder.column().computedId(id).name(name).type(Type.STRING).headerSize(12).empty(0).invalid(2)
                .valid(5).build();
    }
}
