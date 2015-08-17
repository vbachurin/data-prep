package org.talend.dataprep.transformation.api.action.metadata.date;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.talend.dataprep.api.dataset.ColumnMetadata.Builder.column;
import static org.talend.dataprep.transformation.api.action.metadata.ActionMetadataTestUtils.getColumn;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.api.preparation.Action;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.DataSetRowAction;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;
import org.talend.dataprep.transformation.api.action.metadata.ActionMetadataTestUtils;
import org.talend.dataprep.transformation.api.action.metadata.category.ActionCategory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Unit test for the ChangeDatePattern action.
 *
 * @see ChangeDatePattern
 */
public class ChangeDatePatternTest {

    /** The action to test. */
    private ChangeDatePattern action;

    private Map<String, String> parameters;

    @Before
    public void init() throws IOException {
        action = new ChangeDatePattern();

        parameters = ActionMetadataTestUtils.parseParameters( //
                action, //
                ChangeDatePatternTest.class.getResourceAsStream("changeDatePatternAction.json"));
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
    public void testCategory() throws Exception {
        assertThat(action.getCategory(), is(ActionCategory.DATE.getDisplayName()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_check_column_id_parameter_when_dealing_with_row_metadata() {
        action.beforeApply(new HashMap<>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_check_new_pattern_parameter_when_dealing_with_row_metadata() {
        //given
        Map<String, String> missingParameters = new HashMap<>();
        missingParameters.put("column_id", "0000");
        missingParameters.put(ChangeDatePattern.NEW_PATTERN, "toto");

        //when
        action.beforeApply(missingParameters);
    }

    @Test
    public void should_change_column_metadata() throws IOException {
        // given
        String statistics = IOUtils.toString(ChangeDatePatternTest.class.getResourceAsStream("statistics_yyyy-MM-dd.json"));
        ColumnMetadata column = ColumnMetadata.Builder.column().id(1).name("due_date").statistics(statistics).type(Type.DATE)
                .build();
        RowMetadata rowMetadata = new RowMetadata(Collections.singletonList(column));

        // when
        action.beforeApply(parameters);
        action.applyOnColumn(new DataSetRow(rowMetadata), new TransformationContext(), parameters, "0001");

        // then
        ObjectMapper mapper = new ObjectMapper(new JsonFactory());
        JsonNode rootNode = mapper.readTree(column.getStatistics());
        String actualPattern = rootNode.get("patternFrequencyTable").get(0).get("pattern").textValue();
        assertEquals("dd - MMM - yyyy", actualPattern);
    }

    @Test
    public void toto() {
        action.getItems();
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_check_new_pattern_parameter_when_dealing_with_row() {
        //given
        final Map<String, String> insufficientParams = new HashMap<>();
        insufficientParams.put("column_id", "0000");

        //when
        action.beforeApply(insufficientParams);
    }

    @Test
    public void should_process_row() throws Exception {
        // given
        final Map<String, String> values = new HashMap<>();
        values.put("0000", "toto");
        values.put("0001", "04/25/1999");
        values.put("0002", "tata");
        final DataSetRow row = new DataSetRow(values);
        setStatistics(row, "0001", ChangeDatePatternTest.class.getResourceAsStream("statistics_MM_dd_yyyy.json"));

        final Map<String, String> expectedValues = new HashMap<>();
        expectedValues.put("0000", "toto");
        expectedValues.put("0001", "25 - Apr - 1999");
        expectedValues.put("0002", "tata");

        // when
        action.beforeApply(parameters);
        action.applyOnColumn(row, new TransformationContext(), parameters, "0001");

        // then
        assertEquals(expectedValues, row.values());
    }

    @Test
    public void should_process_row_when_value_does_not_match_pattern() throws Exception {
        // given
        Map<String, String> values = new HashMap<>();
        values.put("0000", "toto");
        values.put("0001", "04-25-09");
        values.put("0002", "tata");
        DataSetRow row = new DataSetRow(values);
        setStatistics(row, "0001", ChangeDatePatternTest.class.getResourceAsStream("statistics_MM_dd_yyyy.json"));

        final Map<String, String> expectedValues = new HashMap<>();
        expectedValues.put("0000", "toto");
        expectedValues.put("0001", "25 - Apr - 2009");
        expectedValues.put("0002", "tata");

        // when
        action.beforeApply(parameters);
        action.applyOnColumn(row, new TransformationContext(), parameters, "0001");

        // then
        assertEquals(expectedValues, row.values());
    }

    @Test
    public void should_process_row_when_value_is_empty() throws Exception {
        // given
        Map<String, String> values = new HashMap<>();
        values.put("0000", "toto");
        values.put("0001", "");
        values.put("0002", "tata");
        DataSetRow row = new DataSetRow(values);
        setStatistics(row, "0001", ChangeDatePatternTest.class.getResourceAsStream("statistics_MM_dd_yyyy.json"));

        // when
        action.beforeApply(parameters);
        action.applyOnColumn(row, new TransformationContext(), parameters, "0001");

        // then (values should be unchanged)
        assertEquals(values, row.values());
    }

    @Test
    public void testSuperParse() throws ParseException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        TemporalAccessor date = LocalDate.of(2015, 8, 17);
        String expected = dtf.format(date);

        List<DateTimeFormatter> patterns = new ArrayList<>();
        patterns.add(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        patterns.add(DateTimeFormatter.ofPattern("MM-dd-yy"));
        patterns.add(DateTimeFormatter.ofPattern("yy/dd/MM"));

        assertEquals(expected, dtf.format(action.superParse("2015/08/17", patterns)));
        assertEquals(expected, dtf.format(action.superParse("08-17-15", patterns)));
        assertEquals(expected, dtf.format(action.superParse("15/17/08", patterns)));
    }

    @Test
    public void should_accept_column() {
        assertTrue(action.acceptColumn(getColumn(Type.DATE)));
    }

    @Test
    public void should_not_accept_column() {
        assertFalse(action.acceptColumn(getColumn(Type.NUMERIC)));
        assertFalse(action.acceptColumn(getColumn(Type.FLOAT)));
        assertFalse(action.acceptColumn(getColumn(Type.STRING)));
        assertFalse(action.acceptColumn(getColumn(Type.BOOLEAN)));
    }
}
