package org.talend.dataprep.transformation.api.action.metadata.date;

import static org.junit.Assert.*;
import static org.talend.dataprep.transformation.api.action.metadata.ActionMetadataTestUtils.getColumn;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
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

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Unit test for the ChangeDatePattern action.
 * 
 * @see ChangeDatePattern
 */
public class ChangeDatePatternTest {

    /** The row consumer to test. */
    private BiConsumer<DataSetRow, TransformationContext> rowClosure;

    /** The metadata consumer to test. */
    private BiConsumer<RowMetadata, TransformationContext> metadataClosure;

    /** The action to test. */
    private ChangeDatePattern action;

    /**
     * Constructor.
     */
    public ChangeDatePatternTest() throws IOException {
        action = new ChangeDatePattern();

        Map<String, String> parameters = ActionMetadataTestUtils.parseParameters( //
                action, //
                ChangeDatePatternTest.class.getResourceAsStream("changeDatePatternAction.json"));

        rowClosure = action.create(parameters);
        metadataClosure = action.createMetadataClosure(parameters);
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_check_column_id_parameter_when_dealing_with_row_metadata() {
        action.createMetadataClosure(new HashMap<>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_check_new_pattern_parameter_when_dealing_with_row_metadata() {
        Map<String, String> missingParameters = new HashMap<>();
        missingParameters.put(ChangeDatePattern.COLUMN_ID, "0000");
        missingParameters.put(ChangeDatePattern.NEW_PATTERN, "toto");
        action.createMetadataClosure(missingParameters);
    }

    @Test
    public void should_change_column_metadata() throws IOException {
        // given
        ColumnMetadata column = ColumnMetadata.Builder.column().id(1).name("due_date").type(Type.DATE).build();
        RowMetadata rowMetadata = new RowMetadata(Arrays.asList(column));
        String statistics = IOUtils.toString(ChangeDatePatternTest.class.getResourceAsStream("statistics.json"));
        column.setStatistics(statistics);

        // when
        TransformationContext context = new TransformationContext();
        metadataClosure.accept(rowMetadata, context);

        // then
        assertEquals("yyyy-MM-dd", context.get(ChangeDatePattern.OLD_PATTERN));

        ObjectMapper mapper = new ObjectMapper(new JsonFactory());
        JsonNode rootNode = mapper.readTree(column.getStatistics());
        String actualPattern = rootNode.get("patternFrequencyTable").get(0).get("pattern").textValue();
        assertEquals("dd - MMM - yyyy", actualPattern);
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_check_column_id_when_dealing_with_row() {
        action.createMetadataClosure(new HashMap<>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_check_new_pattern_parameter_when_dealing_with_row() {
        Map<String, String> insufficientParams = new HashMap<>();
        insufficientParams.put(ChangeDatePattern.COLUMN_ID, "0000");
        action.create(insufficientParams);
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
        context.put(ChangeDatePattern.OLD_PATTERN, "MM/dd/yyyy");

        // when
        rowClosure.accept(row, context);

        // then
        Map<String, String> expectedValues = new HashMap<>();
        expectedValues.put("0000", "toto");
        expectedValues.put("0001", "25 - Apr - 1999");
        expectedValues.put("0002", "tata");

        assertEquals(expectedValues, row.values());

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
