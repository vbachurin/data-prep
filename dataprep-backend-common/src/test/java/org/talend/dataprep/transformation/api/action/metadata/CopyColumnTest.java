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
import static org.junit.Assert.assertTrue;
import static org.talend.dataprep.transformation.api.action.metadata.ActionMetadataTestUtils.getColumn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.junit.Test;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;

/**
 * Test class for Split action. Creates one consumer, and test it.
 * 
 * @see CopyColumn
 */
public class CopyColumnTest {

    /** The row consumer to test. */
    private BiConsumer<DataSetRow, TransformationContext> rowClosure;

    /** The metadata consumer to test. */
    private Consumer<RowMetadata> metadataClosure;

    /** The action to test. */
    private CopyColumn action;

    /**
     * Constructor.
     */
    public CopyColumnTest() throws IOException {
        action = new CopyColumn();

        Map<String, String> parameters = ActionMetadataTestUtils.parseParameters( //
                action, //
                CopyColumnTest.class.getResourceAsStream("copyColumnAction.json"));

        rowClosure = action.create(parameters);
        metadataClosure = action.createMetadataClosure(parameters);
    }

    /**
     * @see Split#create(Map)
     */
    @Test
    public void should_split_row() {
        Map<String, String> values = new HashMap<>();
        values.put("recipe", "lorem bacon");
        values.put("steps", "Bacon ipsum dolor amet swine leberkas pork belly");
        values.put("last update", "01/01/2015");
        DataSetRow row = new DataSetRow(values);

        Map<String, String> expectedValues = new HashMap<>();
        expectedValues.put("recipe", "lorem bacon");
        expectedValues.put("steps", "Bacon ipsum dolor amet swine leberkas pork belly");
        expectedValues.put("steps_copy", "Bacon ipsum dolor amet swine leberkas pork belly");
        expectedValues.put("last update", "01/01/2015");

        rowClosure.accept(row, new TransformationContext());
        assertEquals(expectedValues, row.values());
    }

    /**
     * @see Split#createMetadataClosure(Map)
     */
    @Test
    public void should_update_metadata() {

        List<ColumnMetadata> input = new ArrayList<>();
        input.add(createMetadata("recipe", "recipe"));
        input.add(createMetadata("steps", "steps"));
        input.add(createMetadata("last update", "last update"));
        RowMetadata rowMetadata = new RowMetadata(input);

        metadataClosure.accept(rowMetadata);
        List<ColumnMetadata> actual = rowMetadata.getColumns();

        List<ColumnMetadata> expected = new ArrayList<>();
        expected.add(createMetadata("recipe", "recipe"));
        expected.add(createMetadata("steps", "steps"));
        expected.add(createMetadata("steps_copy", "steps_copy"));
        expected.add(createMetadata("last update", "last update"));

        assertEquals(expected, actual);
    }

    @Test
    public void should_accept_column() {
        assertTrue(action.accept(getColumn(Type.ANY)));
    }

    @Test
    public void should_not_accept_column() {
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
