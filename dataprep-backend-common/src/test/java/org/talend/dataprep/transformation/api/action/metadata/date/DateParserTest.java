package org.talend.dataprep.transformation.api.action.metadata.date;

import static org.junit.Assert.assertEquals;
import static org.talend.dataprep.transformation.api.action.metadata.ActionMetadataTestUtils.getColumn;
import static org.talend.dataprep.transformation.api.action.metadata.ActionMetadataTestUtils.setStatistics;

import java.io.IOException;
import java.text.ParseException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.statistics.PatternFrequency;
import org.talend.dataprep.api.type.Type;

/**
 * Unit test for the DateParser class.
 * @see DateParser
 */
public class DateParserTest extends BaseDateTests {

    /** The action to test. */
    @Autowired
    private DateParser action;

    @Test(expected = DateTimeException.class)
    public void shouldNotParseNull() {
        action.parseDateFromPatterns(null, Collections.emptyList());
    }

    @Test
    public void shouldSortPatterns() throws IOException {

        // given
        Map<String, String> values = new HashMap<>();
        values.put("0000", "toto");
        values.put("0001", "04/25/1999");
        values.put("0002", "tata");
        DataSetRow row = new DataSetRow(values);
        setStatistics(row, "0001", ChangeDatePatternTest.class.getResourceAsStream("statistics_with_different_test_cases.json"));

        // when
        final List<PatternFrequency> patternFrequencies = row.getRowMetadata().getById("0001").getStatistics().getPatternFrequencies();
        List<DatePattern> actual = action.getPatterns(patternFrequencies);

        // then
        List<DatePattern> expected = new ArrayList<>();
        expected.add(new DatePattern(47, "MM/dd/yyyy"));
        expected.add(new DatePattern(27, "MM-dd-yy"));
        expected.add(new DatePattern(0, "yyyy"));

        assertEquals(expected, actual);
    }

    @Test
    public void testParseDate() throws ParseException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        TemporalAccessor date = LocalDate.of(2015, 8, 17);
        String expected = dtf.format(date);

        List<DatePattern> patterns = new ArrayList<>();
        patterns.add(new DatePattern(1, "yyyy/MM/dd"));
        patterns.add(new DatePattern(1, "MM-dd-yy"));
        patterns.add(new DatePattern(1, "yy/dd/MM"));

        assertEquals(expected, dtf.format(action.parseDateFromPatterns("2015/08/17", action.computeDateTimeFormatter(patterns))));
        assertEquals(expected, dtf.format(action.parseDateFromPatterns("08-17-15", action.computeDateTimeFormatter(patterns))));
        assertEquals(expected, dtf.format(action.parseDateFromPatterns("15/17/08", action.computeDateTimeFormatter(patterns))));
    }

    @Test
    public void testParseDateWithWeirdPattern() throws ParseException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        TemporalAccessor date = LocalDate.of(2015, 8, 17);
        String expected = dtf.format(date);

        List<DatePattern> patterns = new ArrayList<>();
        patterns.add(new DatePattern(10, "aaaaaaa"));
        patterns.add(new DatePattern(2, "yyyy/MM/dd"));

        assertEquals(expected, dtf.format(action.parseDateFromPatterns("2015/08/17", action.computeDateTimeFormatter(patterns))));
    }

    @Test
    public void testParseDateWithEmptyPattern() throws ParseException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        TemporalAccessor date = LocalDate.of(2015, 8, 17);
        String expected = dtf.format(date);

        List<DatePattern> patterns = new ArrayList<>();
        patterns.add(new DatePattern(1, ""));
        patterns.add(new DatePattern(2, "yyyy/MM/dd"));

        assertEquals(expected, dtf.format(action.parseDateFromPatterns("2015/08/17", action.computeDateTimeFormatter(patterns))));
    }

    @Test
    public void shouldComputePatternFromDQ() {
        assertEquals(new DatePattern(1, "d/M/yyyy"), action.guessPattern("01/02/2015"));
        assertEquals(new DatePattern(1, "yyyy-M-d"), action.guessPattern("2015-01-02"));
        assertEquals(new DatePattern(1, "9999"), action.guessPattern("2015"));
        assertEquals(new DatePattern(1, "MMMM d yyyy"), action.guessPattern("July 14 2015"));
    }

    @Test(expected = DateTimeException.class)
    public void shouldNotComputePatternFromDQBecauseEmptyValue() {
        action.guessPattern("");
    }

    @Test(expected = DateTimeException.class)
    public void shouldNotComputePatternFromDQBecauseNullValue() {
        action.guessPattern(null);
    }

    @Test(expected = DateTimeException.class)
    public void shouldNotComputePatternFromDQBecauseInvalidValue() {
        action.guessPattern("not a date");
    }


    @Test
    public void shouldUpdateColumnStatisticsWithNewDatePattern() {

        // given
        ColumnMetadata column = getColumn(Type.DATE);
        column.getStatistics().getPatternFrequencies().add(new PatternFrequency("yyyy", 19));

        // when
        action.guessAndParse("01/02/2015", column);

        // then
        final List<PatternFrequency> actual = column.getStatistics().getPatternFrequencies();
        assertEquals(2, actual.size());
        assertEquals(new PatternFrequency("d/M/yyyy", 1), actual.get(1));
    }

    @Test
    public void testComputePatterns() {
        // given
        List<DatePattern> patterns = new ArrayList<>();
        patterns.add(new DatePattern(1, "yy/dd/MM"));
        patterns.add(new DatePattern(1, null)); // null
        patterns.add(new DatePattern(1, "yyyy/MM/dd"));
        patterns.add(new DatePattern(1, "")); // empty
        patterns.add(new DatePattern(1, "MM-dd-yy"));

        // when
        List<DatePattern> actual = action.computeDateTimeFormatter(patterns);

        // then
        assertEquals(3, actual.size());
    }

}
