package org.talend.dataprep.transformation.api.action.metadata.date;

import org.junit.Before;
import org.junit.Test;
import org.talend.dataprep.transformation.api.action.metadata.ActionMetadataTestUtils;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;

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

    @Test
    public void testSuperParse() throws ParseException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        TemporalAccessor date = LocalDate.of(2015, 8, 17);
        String expected = dtf.format(date);

        List<String> patterns = new ArrayList<>();
        patterns.add("yyyy/MM/dd");
        patterns.add("MM-dd-yy");
        patterns.add("yy/dd/MM");

        assertEquals(expected, dtf.format(action.superParse("2015/08/17", action.computePatterns(patterns))));
        assertEquals(expected, dtf.format(action.superParse("08-17-15", action.computePatterns(patterns))));
        assertEquals(expected, dtf.format(action.superParse("15/17/08", action.computePatterns(patterns))));
    }

    @Test
    public void testSuperParseWithZarbPattern() throws ParseException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        TemporalAccessor date = LocalDate.of(2015, 8, 17);
        String expected = dtf.format(date);

        List<String> patterns = new ArrayList<>();
        patterns.add("aaaaaaa");
        patterns.add("yyyy/MM/dd");

        assertEquals(expected, dtf.format(action.superParse("2015/08/17", action.computePatterns(patterns))));
    }

    @Test
    public void testSuperParseWithEmptyPattern() throws ParseException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        TemporalAccessor date = LocalDate.of(2015, 8, 17);
        String expected = dtf.format(date);

        List<String> patterns = new ArrayList<>();
        patterns.add("");
        patterns.add("yyyy/MM/dd");

        assertEquals(expected, dtf.format(action.superParse("2015/08/17", action.computePatterns(patterns))));
    }

    @Test
    public void testComputePatterns() {
        List<String> patterns = new ArrayList<>();
        patterns.add("yyyy/MM/dd");
        patterns.add("MM-dd-yy");
        patterns.add("yy/dd/MM");
        patterns.add(null); // null
        patterns.add(""); // empty
        patterns.add("aaaaaaa"); // invalid
        patterns.add("yy/dd/MM"); // duplicate

        Set<DateTimeFormatter> formatters = action.computePatterns(patterns);

        // assert that null, invalid, empty and duplicate are excluded:
        assertEquals(3, formatters.size());
    }
}
