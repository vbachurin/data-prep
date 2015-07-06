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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Test class for ExtractEmailDomain action. Creates one consumer, and test it.
 * 
 * @see ExtractEmailDomain
 */
public class ExtractEmailDomainTest {

    /** The row consumer to test. */
    private BiConsumer<DataSetRow, TransformationContext> rowClosure;

    /** The metadata consumer to test. */
    private BiConsumer<RowMetadata, TransformationContext> metadataClosure;

    /** The action to test. */
    private ExtractEmailDomain action;

    /**
     * Default empty constructor.
     */
    public ExtractEmailDomainTest() throws IOException {
        String actions = IOUtils.toString(ExtractEmailDomainTest.class.getResourceAsStream("extractDomainAction.json"));
        ObjectMapper mapper = new ObjectMapper(new JsonFactory());
        String content = actions.trim();
        JsonNode node = mapper.readTree(content);
        action = new ExtractEmailDomain();
        Map<String, String> parameters = action.parseParameters(node.get("actions").get(0).get("parameters").fields());
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

    @Test
    public void should_accept_column() {
        ColumnMetadata column = getColumn(Type.STRING);
        column.setDomain("email");
        assertTrue(action.accept(column));
    }

    @Test
    public void should_not_accept_column() {
        assertFalse(action.accept(getColumn(Type.STRING)));
        assertFalse(action.accept(getColumn(Type.DATE)));
        assertFalse(action.accept(getColumn(Type.BOOLEAN)));
        assertFalse(action.accept(getColumn(Type.NUMERIC)));
        assertFalse(action.accept(getColumn(Type.INTEGER)));
        assertFalse(action.accept(getColumn(Type.DOUBLE)));
        assertFalse(action.accept(getColumn(Type.FLOAT)));

        ColumnMetadata column = getColumn(Type.STRING);
        column.setDomain("not an email");
        assertFalse(action.accept(column));
    }
}
