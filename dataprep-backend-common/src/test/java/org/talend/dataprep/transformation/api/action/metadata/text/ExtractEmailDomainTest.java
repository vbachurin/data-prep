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
package org.talend.dataprep.transformation.api.action.metadata.text;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.talend.dataprep.api.dataset.ColumnMetadata.Builder.column;
import static org.talend.dataprep.transformation.api.action.metadata.ActionMetadataTestUtils.getColumn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.api.preparation.Action;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.DataSetRowAction;
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
    private DataSetRowAction rowClosure;

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
        final Action action = this.action.create(parameters);
        rowClosure = action.getRowAction();
    }

    @Test
    public void testAdapt() throws Exception {
        assertThat(action.adapt(null), is(action));
        ColumnMetadata column = column().name("myColumn").id(0).type(Type.STRING).build();
        assertThat(action.adapt(column), is(action));
    }

    /**
     * @see Split#create(Map)
     */
    @Test
    public void test_values() {
        Map<String, String> values = new HashMap<>();
        values.put("0000", "lorem bacon");
        values.put("0001", "david.bowie@yopmail.com");
        values.put("0002", "01/01/2015");
        DataSetRow row = new DataSetRow(values);

        Map<String, String> expectedValues = new HashMap<>();
        expectedValues.put("0000", "lorem bacon");
        expectedValues.put("0001", "david.bowie@yopmail.com");
        expectedValues.put("0003", "david.bowie");
        expectedValues.put("0004", "yopmail.com");
        expectedValues.put("0002", "01/01/2015");

        row = rowClosure.apply(row, new TransformationContext());
        assertEquals(expectedValues, row.values());
    }

    @Test
    public void test_values_invalid() {
        Map<String, String> values = new HashMap<>();
        values.put("0000", "lorem bacon");
        values.put("0001", "david.bowie");
        values.put("0002", "01/01/2015");
        DataSetRow row = new DataSetRow(values);

        Map<String, String> expectedValues = new HashMap<>();
        expectedValues.put("0000", "lorem bacon");
        expectedValues.put("0001", "david.bowie");
        expectedValues.put("0003", "");
        expectedValues.put("0004", "");
        expectedValues.put("0002", "01/01/2015");

        row = rowClosure.apply(row, new TransformationContext());
        assertEquals(expectedValues, row.values());
    }

    /**
     * @see Action#getMetadataAction()
     */
    @Test
    public void test_metadata() {
        List<ColumnMetadata> input = new ArrayList<>();
        input.add(createMetadata("0000", "recipe"));
        input.add(createMetadata("0001", "email"));
        input.add(createMetadata("0002", "last update"));
        RowMetadata rowMetadata = new RowMetadata(input);

        DataSetRow row = new DataSetRow(rowMetadata);
        row = rowClosure.apply(row, new TransformationContext());
        List<ColumnMetadata> actual = row.getRowMetadata().getColumns();

        List<ColumnMetadata> expected = new ArrayList<>();
        expected.add(createMetadata("0000", "recipe"));
        expected.add(createMetadata("0001", "email"));
        expected.add(createMetadata("0003", "email_local"));
        expected.add(createMetadata("0004", "email_domain"));
        expected.add(createMetadata("0002", "last update"));

        assertEquals(expected, actual);
    }

    /**
     * @see Action#getMetadataAction()
     */
    @Test
    public void test_metadata_with_multiple_executions() {
        List<ColumnMetadata> input = new ArrayList<>();
        input.add(createMetadata("0000", "recipe"));
        input.add(createMetadata("0001", "email"));
        input.add(createMetadata("0002", "last update"));
        RowMetadata rowMetadata = new RowMetadata(input);

        rowClosure.apply(new DataSetRow(rowMetadata), new TransformationContext());
        rowClosure.apply(new DataSetRow(rowMetadata), new TransformationContext());
        List<ColumnMetadata> actual = rowMetadata.getColumns();

        List<ColumnMetadata> expected = new ArrayList<>();
        expected.add(createMetadata("0000", "recipe"));
        expected.add(createMetadata("0001", "email"));
        expected.add(createMetadata("0005", "email_local"));
        expected.add(createMetadata("0006", "email_domain"));
        expected.add(createMetadata("0003", "email_local"));
        expected.add(createMetadata("0004", "email_domain"));
        expected.add(createMetadata("0002", "last update"));

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
        assertTrue(action.acceptColumn(column));
    }

    @Test
    public void should_not_accept_column() {
        assertFalse(action.acceptColumn(getColumn(Type.STRING)));
        assertFalse(action.acceptColumn(getColumn(Type.DATE)));
        assertFalse(action.acceptColumn(getColumn(Type.BOOLEAN)));
        assertFalse(action.acceptColumn(getColumn(Type.NUMERIC)));
        assertFalse(action.acceptColumn(getColumn(Type.INTEGER)));
        assertFalse(action.acceptColumn(getColumn(Type.DOUBLE)));
        assertFalse(action.acceptColumn(getColumn(Type.FLOAT)));

        ColumnMetadata column = getColumn(Type.STRING);
        column.setDomain("not an email");
        assertFalse(action.acceptColumn(column));
    }
}
