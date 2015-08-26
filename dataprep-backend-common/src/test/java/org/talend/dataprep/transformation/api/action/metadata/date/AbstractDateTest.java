package org.talend.dataprep.transformation.api.action.metadata.date;

import static org.junit.Assert.assertEquals;
import static org.talend.dataprep.transformation.api.action.metadata.ActionMetadataTestUtils.setStatistics;

import java.io.IOException;
import java.text.ParseException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;

import org.junit.Before;
import org.junit.Test;
import org.talend.dataprep.api.dataset.DataSetRow;

/**
 * Created by stef on 17/08/15.
 */
public class AbstractDateTest {

    /** The action to test. */
    private AbstractDate action;

    @Before
    public void init() throws IOException {
        action = new AbstractDate() {
            @Override
            public String getName() {
                return null;
            }
        };
    }

    @Test(expected = DateTimeException.class)
    public void shouldNotParseNull() {
        action.superParse(null, Collections.emptyList());
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
        List<DatePattern> actual = action.getPatterns(row, "0001");

        // then
        List<DatePattern> expected = new ArrayList<>();
        expected.add(new DatePattern(47, "MM/dd/yyyy"));
        expected.add(new DatePattern(27, "MM-dd-yy"));
        expected.add(new DatePattern(0, "yyyy"));

        assertEquals(expected, actual);
    }

    @Test
    public void testSuperParse() throws ParseException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        TemporalAccessor date = LocalDate.of(2015, 8, 17);
        String expected = dtf.format(date);

        List<DatePattern> patterns = new ArrayList<>();
        patterns.add(new DatePattern(1, "yyyy/MM/dd"));
        patterns.add(new DatePattern(1, "MM-dd-yy"));
        patterns.add(new DatePattern(1, "yy/dd/MM"));

        assertEquals(expected, dtf.format(action.superParse("2015/08/17", action.computeDateTimeFormatter(patterns))));
        assertEquals(expected, dtf.format(action.superParse("08-17-15", action.computeDateTimeFormatter(patterns))));
        assertEquals(expected, dtf.format(action.superParse("15/17/08", action.computeDateTimeFormatter(patterns))));
    }

    @Test
    public void testSuperParseWithZarbPattern() throws ParseException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        TemporalAccessor date = LocalDate.of(2015, 8, 17);
        String expected = dtf.format(date);

        List<DatePattern> patterns = new ArrayList<>();
        patterns.add(new DatePattern(10, "aaaaaaa"));
        patterns.add(new DatePattern(2, "yyyy/MM/dd"));

        assertEquals(expected, dtf.format(action.superParse("2015/08/17", action.computeDateTimeFormatter(patterns))));
    }

    @Test
    public void testSuperParseWithEmptyPattern() throws ParseException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        TemporalAccessor date = LocalDate.of(2015, 8, 17);
        String expected = dtf.format(date);

        List<DatePattern> patterns = new ArrayList<>();
        patterns.add(new DatePattern(1, ""));
        patterns.add(new DatePattern(2, "yyyy/MM/dd"));

        assertEquals(expected, dtf.format(action.superParse("2015/08/17", action.computeDateTimeFormatter(patterns))));
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
