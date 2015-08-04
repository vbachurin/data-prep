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

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.YEARS;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.talend.dataprep.api.dataset.ColumnMetadata.Builder.column;
import static org.talend.dataprep.transformation.api.action.metadata.ActionMetadataTestUtils.getColumn;
import static org.talend.dataprep.transformation.api.action.metadata.date.ComputeTimeSince.TIME_UNIT_PARAMETER;

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
import org.junit.Before;
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
import org.talend.dataprep.transformation.api.action.metadata.category.ActionCategory;
import org.talend.dataprep.transformation.api.action.metadata.column.CopyColumnMetadata;

/**
 * Test class for Split action. Creates one consumer, and test it.
 *
 * @see ComputeTimeSince
 */
public class ComputeTimeSinceTest {

    /** The action to test. */
    private ComputeTimeSince action;

    private Map<String, String> parameters;

    @Before
    public void init() throws IOException {
        action = new ComputeTimeSince();

        final ComputeTimeSince currentAction = new ComputeTimeSince();
        final InputStream json = ComputeTimeSince.class.getResourceAsStream("computeTimeSinceAction.json");
        parameters = ActionMetadataTestUtils.parseParameters(currentAction, json);
        parameters.put(TIME_UNIT_PARAMETER, YEARS.name());
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

    /**
     * @see ComputeTimeSince#create(Map)
     */
    @Test
    public void should_compute_years() throws IOException {
        //given
        final DataSetRow row = getDefaultRow("statistics_MM_dd_yyyy.json");

        final String date = "01/01/2010";
        final String result = computeTimeSince(date, "MM/dd/yyyy", YEARS);

        final Map<String, String> expectedValues = new HashMap<>();
        expectedValues.put("0000", "lorem bacon");
        expectedValues.put("0001", date);
        expectedValues.put("0003", result);
        expectedValues.put("0002", "Bacon");

        //when
        action.beforeApply(parameters);
        action.applyOnColumn(row, new TransformationContext(), parameters, "0001");

        //then
        assertEquals(expectedValues, row.values());
    }

    /**
     * @see ComputeTimeSince#create(Map)
     */
    @Test
    public void should_compute_days() throws IOException {
        //given
        final String date = "06/15/2015";
        final String result = computeTimeSince(date, "MM/dd/yyyy", ChronoUnit.DAYS);

        final DataSetRow row = getDefaultRow("statistics_MM_dd_yyyy.json");
        row.set("0001", date);

        final Map<String, String> expectedValues = new HashMap<>();
        expectedValues.put("0000", "lorem bacon");
        expectedValues.put("0001", date);
        expectedValues.put("0003", result);
        expectedValues.put("0002", "Bacon");

        parameters.put(TIME_UNIT_PARAMETER, DAYS.name());

        //when
        action.beforeApply(parameters);
        action.applyOnColumn(row, new TransformationContext(), parameters, "0001");

        //then
        assertEquals(expectedValues, row.values());
    }

    /**
     * @see ComputeTimeSince#create(Map)
     */
    @Test
    public void should_compute_hours() throws IOException {
        //given
        final String date = "07/16/2015 13:00";
        final String result = computeTimeSince(date, "MM/dd/yyyy HH:mm", ChronoUnit.HOURS);

        final DataSetRow row = getDefaultRow("statistics_MM_dd_yyyy_HH_mm.json");
        row.set("0001", date);

        final Map<String, String> expectedValues = new HashMap<>();
        expectedValues.put("0000", "lorem bacon");
        expectedValues.put("0001", date);
        expectedValues.put("0003", result);
        expectedValues.put("0002", "Bacon");

        parameters.put(TIME_UNIT_PARAMETER, HOURS.name());

        //when
        action.beforeApply(parameters);
        action.applyOnColumn(row, new TransformationContext(), parameters, "0001");

        //then
        assertEquals(expectedValues, row.values());
    }

    @Test
    public void should_compute_hours_twice() throws IOException {
        //given
        final String date = "07/16/2015 13:00";
        final String result = computeTimeSince(date, "MM/dd/yyyy HH:mm", ChronoUnit.HOURS);

        final DataSetRow row = getDefaultRow("statistics_MM_dd_yyyy_HH_mm.json");
        row.set("0001", date);

        final Map<String, String> expectedValues = new HashMap<>();
        expectedValues.put("0000", "lorem bacon");
        expectedValues.put("0001", date);
        expectedValues.put("0004", result);
        expectedValues.put("0003", result);
        expectedValues.put("0002", "Bacon");

        parameters.put(TIME_UNIT_PARAMETER, HOURS.name());

        //when
        action.beforeApply(parameters);
        action.applyOnColumn(row, new TransformationContext(), parameters, "0001");
        action.applyOnColumn(row, new TransformationContext(), parameters, "0001");

        //then
        assertEquals(expectedValues, row.values());
    }

    @Test
    public void should_compute_twice_diff_units() throws IOException {
        //given
        final String date = "07/16/2015 12:00";
        final String resultInHours = computeTimeSince(date, "MM/dd/yyyy HH:mm", ChronoUnit.HOURS);
        final String resultInDays = computeTimeSince(date, "MM/dd/yyyy HH:mm", ChronoUnit.DAYS);

        final DataSetRow row = getDefaultRow("statistics_MM_dd_yyyy_HH_mm.json");
        row.set("0001", date);

        final Map<String, String> expectedValues = new HashMap<>();
        expectedValues.put("0000", "lorem bacon");
        expectedValues.put("0001", date);
        expectedValues.put("0004", resultInHours);
        expectedValues.put("0003", resultInDays);
        expectedValues.put("0002", "Bacon");

        //when
        parameters.put(TIME_UNIT_PARAMETER, DAYS.name());
        action.beforeApply(parameters);
        action.applyOnColumn(row, new TransformationContext(), parameters, "0001");

        parameters.put(TIME_UNIT_PARAMETER, HOURS.name());
        action.beforeApply(parameters);
        action.applyOnColumn(row, new TransformationContext(), parameters, "0001");

        //then
        assertEquals(expectedValues, row.values());
    }

    /**
     * @see Action#getRowAction()
     */
    @Test
    public void should_update_metadata() throws IOException {
        //given
        final DataSetRow row = getDefaultRow("statistics_MM_dd_yyyy.json");

        final List<ColumnMetadata> expected = new ArrayList<>();
        expected.add(createMetadata("0000", "recipe"));
        expected.add(createMetadata("0001", "last update", Type.DATE, "statistics_MM_dd_yyyy.json"));
        expected.add(createMetadata("0003", "since_last update_in_years", Type.INTEGER));
        expected.add(createMetadata("0002", "steps"));

        //when
        action.beforeApply(parameters);
        action.applyOnColumn(row, new TransformationContext(), parameters, "0001");

        //then
        assertEquals(expected, row.getRowMetadata().getColumns());
    }

    /**
     * @see Action#getRowAction()
     */
    @Test
    public void should_update_metadata_twice() throws IOException {
        //given
        final DataSetRow row = getDefaultRow("statistics_MM_dd_yyyy.json");

        final List<ColumnMetadata> expected = new ArrayList<>();
        expected.add(createMetadata("0000", "recipe"));
        expected.add(createMetadata("0001", "last update", Type.DATE, "statistics_MM_dd_yyyy.json"));
        expected.add(createMetadata("0004", "since_last update_in_years", Type.INTEGER));
        expected.add(createMetadata("0003", "since_last update_in_years", Type.INTEGER));
        expected.add(createMetadata("0002", "steps"));

        //when
        action.beforeApply(parameters);
        action.applyOnColumn(row, new TransformationContext(), parameters, "0001");
        action.applyOnColumn(row, new TransformationContext(), parameters, "0001");

        //then
        assertEquals(expected, row.getRowMetadata().getColumns());
    }

    /**
     * @see Action#getRowAction()
     */
    @Test
    public void should_update_metadata_twice_diff_units() throws IOException {
        //given
        final DataSetRow row = getDefaultRow("statistics_MM_dd_yyyy.json");

        final List<ColumnMetadata> expected = new ArrayList<>();
        expected.add(createMetadata("0000", "recipe"));
        expected.add(createMetadata("0001", "last update", Type.DATE, "statistics_MM_dd_yyyy.json"));
        expected.add(createMetadata("0004", "since_last update_in_days", Type.INTEGER));
        expected.add(createMetadata("0003", "since_last update_in_years", Type.INTEGER));
        expected.add(createMetadata("0002", "steps"));

        //when
        parameters.put(TIME_UNIT_PARAMETER, YEARS.name());
        action.beforeApply(parameters);
        action.applyOnColumn(row, new TransformationContext(), parameters, "0001");

        parameters.put(TIME_UNIT_PARAMETER, DAYS.name());
        action.beforeApply(parameters);
        action.applyOnColumn(row, new TransformationContext(), parameters, "0001");

        //then
        assertEquals(expected, row.getRowMetadata().getColumns());
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

        parameters.put(TIME_UNIT_PARAMETER, unit);

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
