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
package org.talend.dataprep.transformation.api.action.metadata.date;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.talend.dataprep.api.dataset.ColumnMetadata.Builder.column;
import static org.talend.dataprep.transformation.api.action.metadata.ActionMetadataTestUtils.getColumn;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParsePosition;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.api.preparation.Action;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.DataSetRowAction;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;
import org.talend.dataprep.transformation.api.action.metadata.ActionMetadataTestUtils;

/**
 * Test class for Split action. Creates one consumer, and test it.
 *
 * @see ComputeTimeSince
 */
public class ComputeTimeSinceTest {

    /** This class' logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ComputeTimeSinceTest.class);

    /** The row consumer to test. */
    private DataSetRowAction rowClosure;

    /** The action to test. */
    private ComputeTimeSince action;

    /**
     * Constructor.
     */
    public ComputeTimeSinceTest() throws IOException {
        action = new ComputeTimeSince();
        rowClosure = getClosure(ChronoUnit.YEARS.name());
    }

    @Test
    public void testAdapt() throws Exception {
        assertThat(action.adapt(null), is(action));
        ColumnMetadata column = column().name("myColumn").id(0).type(Type.STRING).build();
        assertThat(action.adapt(column), is(action));
    }

    /**
     * @see ComputeTimeSince#create(Map)
     */
    @Test
    public void should_compute_years() throws IOException {

        DataSetRow row = getDefaultRow("statistics_MM_dd_yyyy.json");

        String date = "01/01/2010";
        String result = computeTimeSince(date, "MM/dd/yyyy", ChronoUnit.YEARS);
        LOGGER.info("{} years since {}", result, date);

        Map<String, String> expectedValues = new HashMap<>();
        expectedValues.put("0000", "lorem bacon");
        expectedValues.put("0001", date);
        expectedValues.put("0003", result);
        expectedValues.put("0002", "Bacon");

        DataSetRow actual = rowClosure.apply(row, new TransformationContext());
        assertEquals(expectedValues, actual.values());
    }

    /**
     * @see ComputeTimeSince#create(Map)
     */
    @Test
    public void should_compute_days() throws IOException {

        String date = "06/15/2015";
        String result = computeTimeSince(date, "MM/dd/yyyy", ChronoUnit.DAYS);
        LOGGER.info("{} days since {}", result, date);

        DataSetRow row = getDefaultRow("statistics_MM_dd_yyyy.json");
        row.set("0001", date);

        Map<String, String> expectedValues = new HashMap<>();
        expectedValues.put("0000", "lorem bacon");
        expectedValues.put("0001", date);
        expectedValues.put("0003", result);
        expectedValues.put("0002", "Bacon");

        DataSetRowAction closure = getClosure(ChronoUnit.DAYS.name());

        DataSetRow actual = closure.apply(row, new TransformationContext());
        assertEquals(expectedValues, actual.values());
    }

    /**
     * @see ComputeTimeSince#create(Map)
     */
    @Test
    public void should_compute_hours() throws IOException {

        String date = "07/16/2015 13:00";
        String result = computeTimeSince(date, "MM/dd/yyyy HH:mm", ChronoUnit.HOURS);
        LOGGER.info("{} hours since {}", result, date);

        DataSetRow row = getDefaultRow("statistics_MM_dd_yyyy_HH_mm.json");
        row.set("0001", date);

        Map<String, String> expectedValues = new HashMap<>();
        expectedValues.put("0000", "lorem bacon");
        expectedValues.put("0001", date);
        expectedValues.put("0003", result);
        expectedValues.put("0002", "Bacon");

        DataSetRowAction closure = getClosure(ChronoUnit.HOURS.name());

        DataSetRow actual = closure.apply(row, new TransformationContext());
        assertEquals(expectedValues, actual.values());
    }

    @Test
    public void should_compute_hours_twice() throws IOException {
        String date = "07/16/2015 13:00";
        String result = computeTimeSince(date, "MM/dd/yyyy HH:mm", ChronoUnit.HOURS);
        LOGGER.info("{} hours since {}", result, date);

        DataSetRow row = getDefaultRow("statistics_MM_dd_yyyy_HH_mm.json");
        row.set("0001", date);

        Map<String, String> expectedValues = new HashMap<>();
        expectedValues.put("0000", "lorem bacon");
        expectedValues.put("0001", date);
        expectedValues.put("0004", result);
        expectedValues.put("0003", result);
        expectedValues.put("0002", "Bacon");

        DataSetRowAction closure = getClosure(ChronoUnit.HOURS.name());

        DataSetRow actual = closure.apply(row, new TransformationContext());
        actual = closure.apply(actual, new TransformationContext());
        assertEquals(expectedValues, actual.values());
    }

    @Test
    public void should_compute_twice_diff_units() throws IOException {

        String date = "07/16/2015 12:00";
        String resultInHours = computeTimeSince(date, "MM/dd/yyyy HH:mm", ChronoUnit.HOURS);
        LOGGER.info("{} hours since {}", resultInHours, date);
        String resultInDays = computeTimeSince(date, "MM/dd/yyyy HH:mm", ChronoUnit.DAYS);
        LOGGER.info("{} days since {}", resultInDays, date);

        DataSetRow row = getDefaultRow("statistics_MM_dd_yyyy_HH_mm.json");
        row.set("0001", date);

        Map<String, String> expectedValues = new HashMap<>();
        expectedValues.put("0000", "lorem bacon");
        expectedValues.put("0001", date);
        expectedValues.put("0004", resultInHours);
        expectedValues.put("0003", resultInDays);
        expectedValues.put("0002", "Bacon");

        DataSetRowAction closureInDays = getClosure(ChronoUnit.DAYS.name());
        DataSetRow actual = closureInDays.apply(row, new TransformationContext());

        DataSetRowAction closureInHours = getClosure(ChronoUnit.HOURS.name());
        actual = closureInHours.apply(actual, new TransformationContext());

        assertEquals(expectedValues, actual.values());
    }

    /**
     * @see Action#getRowAction()
     */
    @Test
    public void should_update_metadata() throws IOException {
        DataSetRow row = getDefaultRow("statistics_MM_dd_yyyy.json");

        DataSetRowAction closure = getClosure(ChronoUnit.YEARS.name());
        List<ColumnMetadata> actual = closure.apply(row, new TransformationContext()).getRowMetadata().getColumns();

        List<ColumnMetadata> expected = new ArrayList<>();
        expected.add(createMetadata("0000", "recipe"));
        expected.add(createMetadata("0001", "last update", Type.DATE, "statistics_MM_dd_yyyy.json"));
        expected.add(createMetadata("0003", "since_last update_in_years", Type.INTEGER));
        expected.add(createMetadata("0002", "steps"));

        assertEquals(expected, actual);
    }

    /**
     * @see Action#getRowAction()
     */
    @Test
    public void should_update_metadata_twice() throws IOException {
        DataSetRow row = getDefaultRow("statistics_MM_dd_yyyy.json");

        DataSetRowAction closure = getClosure(ChronoUnit.YEARS.name());
        DataSetRow actual = closure.apply(row, new TransformationContext());
        actual = closure.apply(actual, new TransformationContext());

        List<ColumnMetadata> expected = new ArrayList<>();
        expected.add(createMetadata("0000", "recipe"));
        expected.add(createMetadata("0001", "last update", Type.DATE, "statistics_MM_dd_yyyy.json"));
        expected.add(createMetadata("0004", "since_last update_in_years", Type.INTEGER));
        expected.add(createMetadata("0003", "since_last update_in_years", Type.INTEGER));
        expected.add(createMetadata("0002", "steps"));

        assertEquals(expected, actual.getRowMetadata().getColumns());
    }

    /**
     * @see Action#getRowAction()
     */
    @Test
    public void should_update_metadata_twice_diff_units() throws IOException {
        DataSetRow row = getDefaultRow("statistics_MM_dd_yyyy.json");

        DataSetRowAction closure = getClosure(ChronoUnit.YEARS.name());
        DataSetRow actual = closure.apply(row, new TransformationContext());

        DataSetRowAction closureInDays = getClosure(ChronoUnit.DAYS.name());
        actual = closureInDays.apply(actual, new TransformationContext());

        List<ColumnMetadata> expected = new ArrayList<>();
        expected.add(createMetadata("0000", "recipe"));
        expected.add(createMetadata("0001", "last update", Type.DATE, "statistics_MM_dd_yyyy.json"));
        expected.add(createMetadata("0004", "since_last update_in_days", Type.INTEGER));
        expected.add(createMetadata("0003", "since_last update_in_years", Type.INTEGER));
        expected.add(createMetadata("0002", "steps"));

        assertEquals(expected, actual.getRowMetadata().getColumns());
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

    /**
     * Compute time since now.
     *
     * @param date the date to compute from.
     * @param pattern the pattern to use.
     * @param unit the unit for the result.
     * @return time since now in the wanted unit.
     */
    String computeTimeSince(String date, String pattern, ChronoUnit unit) {

        Temporal now = (unit == ChronoUnit.HOURS ? LocalDateTime.now() : LocalDate.now());

        DateTimeFormatter format = DateTimeFormatter.ofPattern(pattern);
        TemporalAccessor start = format.parse(date, new ParsePosition(0));

        Temporal result = (unit == ChronoUnit.HOURS ? LocalDateTime.from(start) : LocalDate.from(start));
        return String.valueOf(unit.between(result, now));
    }

    /**
     * Return a ComputeTimeSince closure with the given unit.
     * 
     * @param unit the unit to use.
     * @return a ComputeTimeSince closure with the given unit.
     */
    private DataSetRowAction getClosure(String unit) throws IOException {
        ComputeTimeSince currentAction = new ComputeTimeSince();
        InputStream json = ComputeTimeSince.class.getResourceAsStream("computeTimeSinceAction.json");
        Map<String, String> parameters = ActionMetadataTestUtils.parseParameters(currentAction, json);

        parameters.put(ComputeTimeSince.TIME_UNIT_PARAMETER, unit);

        Action alternativeAction = currentAction.create(parameters);
        return alternativeAction.getRowAction();
    }

    /**
     * @param statisticsFileName the statistics file name to use.
     * @return a row with default settings for the tests.
     */
    private DataSetRow getDefaultRow(String statisticsFileName) throws IOException {

        List<ColumnMetadata> columns = new ArrayList<>(3);
        columns.add(ColumnMetadata.Builder.column().name("recipe").type(Type.STRING).build());
        String statistics = IOUtils.toString(ComputeTimeSinceTest.class.getResourceAsStream(statisticsFileName));
        columns.add(ColumnMetadata.Builder.column().name("last update").type(Type.DATE).statistics(statistics).build());
        columns.add(ColumnMetadata.Builder.column().name("steps").type(Type.STRING).build());

        RowMetadata metadata = new RowMetadata();
        metadata.setColumns(columns);

        Map<String, String> values = new HashMap<>();
        values.put("0000", "lorem bacon");
        values.put("0001", "01/01/2010");
        values.put("0002", "Bacon");

        return new DataSetRow(metadata, values);
    }

    private ColumnMetadata createMetadata(String id, String name, Type type, String statisticsFileName) throws IOException {
        ColumnMetadata column = createMetadata(id, name, type);
        String statistics = IOUtils.toString(ComputeTimeSinceTest.class.getResourceAsStream(statisticsFileName));
        column.setStatistics(statistics);
        return column;
    }

    private ColumnMetadata createMetadata(String id, String name) {
        return createMetadata(id, name, Type.STRING);
    }

    private ColumnMetadata createMetadata(String id, String name, Type type) {
        return ColumnMetadata.Builder.column().computedId(id).name(name).type(type).build();
    }

}
