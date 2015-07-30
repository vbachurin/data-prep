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
package org.talend.dataprep.transformation.api.action.metadata.column;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;
import static org.talend.dataprep.api.dataset.ColumnMetadata.Builder.column;
import static org.talend.dataprep.transformation.api.action.metadata.ActionMetadataTestUtils.getColumn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.api.preparation.Action;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.DataSetRowAction;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;
import org.talend.dataprep.transformation.api.action.metadata.ActionMetadataTestUtils;
import org.talend.dataprep.transformation.api.action.parameters.Parameter;

/**
 * Test class for Rename action. Creates one consumer, and test it.
 *
 * @see Rename
 */
public class RenameTest {

    private final DataSetRowAction metadataClosure;

    /** The action to test. */
    private Rename action;

    /**
     * Constructor.
     */
    public RenameTest() throws IOException {
        action = new Rename();
        Map<String, String> parameters = ActionMetadataTestUtils.parseParameters( //
                action, //
                RenameTest.class.getResourceAsStream("renameAction.json"));
        metadataClosure = action.create(parameters).getRowAction();
    }

    @Test
    public void testAdapt() throws Exception {
        assertThat(action.adapt(null), is(action));
        ColumnMetadata column = column().name("myColumn").id(0).type(Type.STRING).build();
        assertThat(action.adapt(column), not(is(action)));
        boolean hasMetExpectedParameter = false;
        for (Parameter parameter : action.adapt(column).getParameters()) {
            if (Rename.NEW_COLUMN_NAME_PARAMETER_NAME.equals(parameter.getName())) {
                assertThat(parameter.getDefault(), is(column.getName()));
                hasMetExpectedParameter = true;
            }
        }
        assertThat(hasMetExpectedParameter, is(true));
    }

    /**
     * @see Action#getMetadataAction()
     */
    @Test
    public void should_update_metadata() {

        List<ColumnMetadata> input = new ArrayList<>();
        ColumnMetadata metadata = //
                column() //
                .id(1) //
                .name("first name") //
                .type(Type.STRING) //
                .headerSize(102) //
                .empty(0) //
                .invalid(2) //
                .valid(5) //
                .build();
        input.add(metadata);
        RowMetadata rowMetadata = new RowMetadata(input);

        metadataClosure.apply(new DataSetRow(rowMetadata), new TransformationContext());

        List<ColumnMetadata> actual = rowMetadata.getColumns();

        ColumnMetadata renamedMetadata = column() //
                .id(1) //
                .name("NAME_FIRST") //
                .type(Type.STRING) //
                .headerSize(102) //
                .empty(0) //
                .invalid(2) //
                .valid(5) //
                .build();
        List<ColumnMetadata> expected = new ArrayList<>();
        expected.add(renamedMetadata);

        assertEquals(expected, actual);
    }

    @Test
    public void should_accept_column() {
        assertTrue(action.acceptColumn(getColumn(Type.STRING)));
        assertTrue(action.acceptColumn(getColumn(Type.NUMERIC)));
        assertTrue(action.acceptColumn(getColumn(Type.FLOAT)));
        assertTrue(action.acceptColumn(getColumn(Type.DATE)));
        assertTrue(action.acceptColumn(getColumn(Type.BOOLEAN)));
    }

}
