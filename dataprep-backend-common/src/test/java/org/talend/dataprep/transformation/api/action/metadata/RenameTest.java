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
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import org.junit.Test;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;

/**
 * Test class for Rename action. Creates one consumer, and test it.
 * 
 * @see Rename
 */
public class RenameTest {

    /** The metadata consumer to test. */
    private BiConsumer<RowMetadata, TransformationContext> metadataClosure;

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
        metadataClosure = action.createMetadataClosure(parameters);
    }

    /**
     * @see Rename#createMetadataClosure(Map)
     */
    @Test
    public void should_update_metadata() {

        List<ColumnMetadata> input = new ArrayList<>();
        ColumnMetadata metadata = ColumnMetadata.Builder //
                .column() //
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

        metadataClosure.accept(rowMetadata, new TransformationContext());

        List<ColumnMetadata> actual = rowMetadata.getColumns();

        ColumnMetadata renamedMetadata = ColumnMetadata.Builder.column() //
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
        assertTrue(action.accept(getColumn(Type.STRING)));
        assertTrue(action.accept(getColumn(Type.NUMERIC)));
        assertTrue(action.accept(getColumn(Type.FLOAT)));
        assertTrue(action.accept(getColumn(Type.DATE)));
        assertTrue(action.accept(getColumn(Type.BOOLEAN)));
    }

}
