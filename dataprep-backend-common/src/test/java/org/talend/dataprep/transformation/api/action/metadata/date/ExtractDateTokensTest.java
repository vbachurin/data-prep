package org.talend.dataprep.transformation.api.action.metadata.date;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.talend.dataprep.api.dataset.ColumnMetadata.Builder.column;
import static org.talend.dataprep.transformation.api.action.metadata.ActionMetadataTestUtils.getColumn;

import java.io.IOException;
import java.io.InputStream;
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
import org.talend.dataprep.transformation.api.action.metadata.ActionMetadataTestUtils;

/**
 * Unit test for the ChangeDatePattern action.
 * 
 * @see ChangeDatePattern
 */
public class ExtractDateTokensTest {

    /** The row consumer to test. */
    private DataSetRowAction rowClosure;

    /** The action to test. */
    private ExtractDateTokens action;

    /**
     * Constructor.
     */
    public ExtractDateTokensTest() throws IOException {
        action = new ExtractDateTokens();

        Map<String, String> parameters = ActionMetadataTestUtils.parseParameters( //
                action, //
                ExtractDateTokensTest.class.getResourceAsStream("extractDateTokensAction.json"));

        final Action action = this.action.create(parameters);
        rowClosure = action.getRowAction();
    }

    private static void setStatistics(DataSetRow row, String columnId, InputStream statisticsContent) throws IOException {
        String statistics = IOUtils.toString(statisticsContent);
        row.getRowMetadata().getById(columnId).setStatistics(statistics);
    }

    @Test
    public void testAdapt() throws Exception {
        assertThat(action.adapt(null), is(action));
        ColumnMetadata column = column().name("myColumn").id(0).type(Type.STRING).build();
        assertThat(action.adapt(column), is(action));
    }

    @Test
    public void should_process_row() throws Exception {

        // given
        Map<String, String> values = new HashMap<>();
        values.put("0000", "toto");
        values.put("0001", "04/25/1999");
        values.put("0002", "tata");
        DataSetRow row = new DataSetRow(values);
        setStatistics(row, "0001", ChangeDatePatternTest.class.getResourceAsStream("statistics_MM_dd_yyyy.json"));

        TransformationContext context = new TransformationContext();

        // when
        row = rowClosure.apply(row, context);

        // then
        Map<String, String> expectedValues = new HashMap<>();
        expectedValues.put("0000", "toto");
        expectedValues.put("0001", "04/25/1999");
        expectedValues.put("0003", "1999");
        expectedValues.put("0004", "4");
        expectedValues.put("0005", "");
        expectedValues.put("0006", "");
        expectedValues.put("0002", "tata");

        assertEquals(expectedValues, row.values());
    }

    @Test
    public void should_process_row_with_time() throws Exception {

        // given
        Map<String, String> values = new HashMap<>();
        values.put("0000", "toto");
        values.put("0001", "04/25/1999 15:45");
        values.put("0002", "tata");
        DataSetRow row = new DataSetRow(values);
        setStatistics(row, "0001", ChangeDatePatternTest.class.getResourceAsStream("statistics_MM_dd_yyyy_HH_mm.json"));

        TransformationContext context = new TransformationContext();

        // when
        row = rowClosure.apply(row, context);

        // then
        Map<String, String> expectedValues = new HashMap<>();
        expectedValues.put("0000", "toto");
        expectedValues.put("0001", "04/25/1999 15:45");
        expectedValues.put("0003", "1999");
        expectedValues.put("0004", "4");
        expectedValues.put("0005", "15");
        expectedValues.put("0006", "45");
        expectedValues.put("0002", "tata");

        assertEquals(expectedValues, row.values());
    }

    @Test
    public void should_process_row_wrong_pattern() throws Exception {

        // given
        Map<String, String> values = new HashMap<>();
        values.put("0000", "toto");
        values.put("0001", "25-04-1999");
        values.put("0002", "tata");
        DataSetRow row = new DataSetRow(values);
        setStatistics(row, "0001", ChangeDatePatternTest.class.getResourceAsStream("statistics_MM_dd_yyyy.json"));
        TransformationContext context = new TransformationContext();

        // when
        row = rowClosure.apply(row, context);

        // then
        Map<String, String> expectedValues = new HashMap<>();
        expectedValues.put("0000", "toto");
        expectedValues.put("0001", "25-04-1999");
        expectedValues.put("0003", "");
        expectedValues.put("0004", "");
        expectedValues.put("0005", "");
        expectedValues.put("0006", "");
        expectedValues.put("0002", "tata");

        assertEquals(expectedValues, row.values());
    }

    @Test
    public void should_update_metadata() throws IOException {

        List<ColumnMetadata> input = new ArrayList<>();
        input.add(createMetadata("0000"));
        input.add(createMetadata("0001"));
        input.add(createMetadata("0002"));
        RowMetadata rowMetadata = new RowMetadata(input);

        String statistics = IOUtils.toString(ChangeDatePatternTest.class.getResourceAsStream("statistics_yyyy-MM-dd.json"));
        input.get(1).setStatistics(statistics);

        rowClosure.apply(new DataSetRow(rowMetadata), new TransformationContext());

        assertNotNull(rowMetadata.getById("0003"));
        assertNotNull(rowMetadata.getById("0004"));
        assertNotNull(rowMetadata.getById("0005"));
        assertNotNull(rowMetadata.getById("0006"));
        assertNull(rowMetadata.getById("0007"));
    }

    private ColumnMetadata createMetadata(String id) {
        return createMetadata(id, Type.STRING);
    }

    private ColumnMetadata createMetadata(String id, Type type) {
        return ColumnMetadata.Builder.column().computedId(id).type(type).headerSize(12).empty(0).invalid(2)
                .valid(5).build();
    }

    @Test
    public void should_accept_column() {
        assertTrue(action.accept(getColumn(Type.DATE)));
    }

    @Test
    public void should_not_accept_column() {
        assertFalse(action.accept(getColumn(Type.NUMERIC)));
        assertFalse(action.accept(getColumn(Type.FLOAT)));
        assertFalse(action.accept(getColumn(Type.STRING)));
        assertFalse(action.accept(getColumn(Type.BOOLEAN)));
    }
}
