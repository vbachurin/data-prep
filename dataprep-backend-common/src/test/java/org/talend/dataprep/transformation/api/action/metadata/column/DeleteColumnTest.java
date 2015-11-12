package org.talend.dataprep.transformation.api.action.metadata.column;

import java.util.*;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;

public class DeleteColumnTest {

    private DeleteColumn DeleteColumn;

    private TransformationContext transformationContext;

    private RowMetadata rowMetadata;

    @Before
    public void init() {
        DeleteColumn = new DeleteColumn();
        transformationContext = new TransformationContext();
        List columns = new ArrayList<>();
        ColumnMetadata columnMetadata = ColumnMetadata.Builder.column() //
                .type(Type.INTEGER) //
                .computedId("0001") //
                .domain("ID") //
                .domainFrequency(1) //
                .domainLabel("Identifier") //
                .build();
        columns.add(columnMetadata);
        columnMetadata = ColumnMetadata.Builder.column() //
                .type(Type.STRING) //
                .computedId("0002") //
                .domain("ANL") //
                .domainFrequency(1) //
                .domainLabel("Animal") //
                .build();
        columns.add(columnMetadata);
        rowMetadata = new RowMetadata();
        rowMetadata.setColumns(columns);
    }

    @Test
    public void shouldDeleteColumn() throws Exception {
        // given
        final Map<String, String> values = new HashMap<>();
        values.put("0001", "1");
        values.put("0002", "Wolf");
        final DataSetRow row = new DataSetRow(rowMetadata, values);
        final Map<String, String> parameters = new HashMap<>();

        // when
        DeleteColumn.applyOnColumn(row, transformationContext, parameters, "0002");

        // then
        final ColumnMetadata column = row.getRowMetadata().getColumns().get(0);
        final int rowSize = row.getRowMetadata().getColumns().size();
        assertEquals(1, rowSize);
        assertFalse(row.values().containsKey("0002"));
        assertEquals(1, row.values().size());
    }

    @Test
    public void shouldDeleteColumnWhenMoreThanOneRow() throws Exception {
        // given
        final Map<String, String> values = new HashMap<>();
        values.put("0001", "1");
        values.put("0002", "Wolf");
        final DataSetRow row = new DataSetRow(rowMetadata, values);

        final Map<String, String> values2 = new HashMap<>();
        values2.put("0001", "2");
        values2.put("0002", "Lion");
        final DataSetRow row2 = new DataSetRow(rowMetadata, values2);
        final Map<String, String> parameters = new HashMap<>();

        // when
        DeleteColumn.applyOnColumn(row, transformationContext, parameters, "0002");
        DeleteColumn.applyOnColumn(row2, transformationContext, parameters, "0002");

        // then
        final int rowSize = row.getRowMetadata().getColumns().size();
        final int row2Size = row2.getRowMetadata().getColumns().size();
        assertEquals(1, rowSize);
        assertEquals(1, row2Size);
        assertFalse(row.values().containsKey("0002"));
        assertFalse(row2.values().containsKey("0002"));
        assertEquals(1, row.values().size());
        assertEquals(1, row2.values().size());
    }

    @Test
    public void shouldDeleteAllColumns() throws Exception {
        // given
        final Map<String, String> values = new HashMap<>();
        values.put("0001", "1");
        values.put("0002", "Wolf");
        final DataSetRow row = new DataSetRow(rowMetadata, values);

        final Map<String, String> values2 = new HashMap<>();
        values.put("0001", "2");
        values.put("0002", "Lion");
        final DataSetRow row2 = new DataSetRow(rowMetadata, values2);
        final Map<String, String> parameters = new HashMap<>();

        // when
        DeleteColumn.applyOnColumn(row, transformationContext, parameters, "0001");
        DeleteColumn.applyOnColumn(row2, transformationContext, parameters, "0001");
        DeleteColumn.applyOnColumn(row, transformationContext, parameters, "0002");
        DeleteColumn.applyOnColumn(row2, transformationContext, parameters, "0002");

        // then
        final int rowSize = row.getRowMetadata().getColumns().size();
        final int row2Size = row2.getRowMetadata().getColumns().size();
        assertEquals(0, rowSize);
        assertEquals(0, row2Size);
        assertFalse(row.values().containsKey("0002"));
        assertFalse(row2.values().containsKey("0002"));
        assertEquals(0, row.values().size());
        assertEquals(0, row2.values().size());
    }

}
