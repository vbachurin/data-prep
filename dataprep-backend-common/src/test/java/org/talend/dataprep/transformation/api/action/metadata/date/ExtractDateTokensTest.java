package org.talend.dataprep.transformation.api.action.metadata.date;

import static org.junit.Assert.*;
import static org.talend.dataprep.transformation.api.action.metadata.ActionMetadataTestUtils.getColumn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
import org.talend.dataprep.transformation.api.action.metadata.ActionMetadataTestUtils;

/**
 * Unit test for the ChangeDatePattern action.
 * 
 * @see ChangeDatePattern
 */
public class ExtractDateTokensTest {

    /** The row consumer to test. */
    private BiConsumer<DataSetRow, TransformationContext> rowClosure;

    /** The metadata consumer to test. */
    private BiConsumer<RowMetadata, TransformationContext> metadataClosure;

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

        rowClosure = action.create(parameters);
        metadataClosure = action.createMetadataClosure(parameters);
    }


    @Test
    public void should_process_row() {

        // given
        Map<String, String> values = new HashMap<>();
        values.put("0000", "toto");
        values.put("0001", "04/25/1999");
        values.put("0002", "tata");
        DataSetRow row = new DataSetRow(values);

        TransformationContext context = new TransformationContext();
        context.put(ExtractDateTokens.PATTERN, "MM/dd/yyyy");

        // when
        rowClosure.accept(row, context);

        // then
        Map<String, String> expectedValues = new HashMap<>();
        expectedValues.put("0000", "toto");
        expectedValues.put("0001", "04/25/1999");
        expectedValues.put("0001_YEAR", "1999");
        expectedValues.put("0001_MONTH", "4");
        expectedValues.put("0002", "tata");

        assertEquals(expectedValues, row.values());
    }

    @Test
    public void should_process_row_wrong_pattern() {

        // given
        Map<String, String> values = new HashMap<>();
        values.put("0000", "toto");
        values.put("0001", "25-04-1999");
        values.put("0002", "tata");
        DataSetRow row = new DataSetRow(values);

        TransformationContext context = new TransformationContext();
        context.put(ExtractDateTokens.PATTERN, "MM/dd/yyyy");

        // when
        rowClosure.accept(row, context);

        // then
        Map<String, String> expectedValues = new HashMap<>();
        expectedValues.put("0000", "toto");
        expectedValues.put("0001", "25-04-1999");
        expectedValues.put("0001_YEAR", "");
        expectedValues.put("0001_MONTH", "");
        expectedValues.put("0002", "tata");

        assertEquals(expectedValues, row.values());
    }

    @Test
    public void should_update_metadata() throws IOException {

        List<ColumnMetadata> input = new ArrayList<>();
        input.add(createMetadata("recipe", "recipe"));
        input.add(createMetadata("0001", "0001"));
        input.add(createMetadata("last update", "last update"));
        RowMetadata rowMetadata = new RowMetadata(input);

        String statistics = IOUtils.toString(ChangeDatePatternTest.class.getResourceAsStream("statistics.json"));
        input.get(1).setStatistics(statistics);

        metadataClosure.accept(rowMetadata, new TransformationContext());
        List<ColumnMetadata> actual = rowMetadata.getColumns();

        List<ColumnMetadata> expected = new ArrayList<>();
        expected.add(createMetadata("recipe", "recipe"));
        expected.add(createMetadata("0001", "0001"));
        expected.add(createMetadata("0001_YEAR", "0001_YEAR", Type.INTEGER));
        expected.add(createMetadata("0001_MONTH", "0001_MONTH", Type.INTEGER));
        expected.add(createMetadata("last update", "last update"));

        assertEquals(expected, actual);
    }

    private ColumnMetadata createMetadata(String id, String name) {
        return createMetadata(id, name, Type.STRING);
    }

    private ColumnMetadata createMetadata(String id, String name, Type type) {
        return ColumnMetadata.Builder.column().computedId(id).name(name).type(type).headerSize(12).empty(0).invalid(2)
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
