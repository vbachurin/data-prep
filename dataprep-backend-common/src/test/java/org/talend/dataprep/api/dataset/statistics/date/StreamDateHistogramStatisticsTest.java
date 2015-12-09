package org.talend.dataprep.api.dataset.statistics.date;

import org.junit.Test;
import org.talend.dataprep.api.dataset.statistics.Range;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.time.Month.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class StreamDateHistogramStatisticsTest {
    @Test
    public void should_create_day_histogram() {
        //given
        final StreamDateHistogramStatistics stats = new StreamDateHistogramStatistics();
        stats.setNumberOfBins(4);

        stats.add(LocalDate.of(2015, JANUARY, 5));
        stats.add(LocalDate.of(2015, JANUARY, 6));
        stats.add(LocalDate.of(2015, JANUARY, 7));
        stats.add(LocalDate.of(2015, JANUARY, 6));
        stats.add(LocalDate.of(2015, JANUARY, 5));
        stats.add(LocalDate.of(2015, JANUARY, 5));

        //when
        final Map<Range<LocalDate>, Long> histogram = stats.getHistogram();

        //then
        assertThat(histogram.size(), is(3));

        final List<Range<LocalDate>> ranges = new ArrayList<>(histogram.keySet());
        Range<LocalDate> range = ranges.get(0);
        assertThat(range.getMin(), is(LocalDate.of(2015, JANUARY, 5)));
        assertThat(range.getMax(), is(LocalDate.of(2015, JANUARY, 6)));
        assertThat(histogram.get(range), is(3L));

        range = ranges.get(1);
        assertThat(range.getMin(), is(LocalDate.of(2015, JANUARY, 6)));
        assertThat(range.getMax(), is(LocalDate.of(2015, JANUARY, 7)));
        assertThat(histogram.get(range), is(2L));

        range = ranges.get(2);
        assertThat(range.getMin(), is(LocalDate.of(2015, JANUARY, 7)));
        assertThat(range.getMax(), is(LocalDate.of(2015, JANUARY, 8)));
        assertThat(histogram.get(range), is(1L));
    }

    @Test
    public void should_create_month_histogram() {
        //given
        final StreamDateHistogramStatistics stats = new StreamDateHistogramStatistics();
        stats.setNumberOfBins(5);

        stats.add(LocalDate.of(2015, JANUARY, 5));
        stats.add(LocalDate.of(2015, JANUARY, 6));
        stats.add(LocalDate.of(2015, MARCH, 15));
        stats.add(LocalDate.of(2015, APRIL, 1));
        stats.add(LocalDate.of(2015, MARCH, 25));
        stats.add(LocalDate.of(2015, JANUARY, 5));
        stats.add(LocalDate.of(2015, JANUARY, 18));

        //when
        final Map<Range<LocalDate>, Long> histogram = stats.getHistogram();

        //then
        assertThat(histogram.size(), is(4));

        final List<Range<LocalDate>> ranges = new ArrayList<>(histogram.keySet());
        Range<LocalDate> range = ranges.get(0);
        assertThat(range.getMin(), is(LocalDate.of(2015, JANUARY, 1)));
        assertThat(range.getMax(), is(LocalDate.of(2015, FEBRUARY, 1)));
        assertThat(histogram.get(range), is(4L));

        range = ranges.get(1);
        assertThat(range.getMin(), is(LocalDate.of(2015, FEBRUARY, 1)));
        assertThat(range.getMax(), is(LocalDate.of(2015, MARCH, 1)));
        assertThat(histogram.get(range), is(0L));

        range = ranges.get(2);
        assertThat(range.getMin(), is(LocalDate.of(2015, MARCH, 1)));
        assertThat(range.getMax(), is(LocalDate.of(2015, APRIL, 1)));
        assertThat(histogram.get(range), is(2L));assertThat(histogram.get(range), is(2L));

        range = ranges.get(3);
        assertThat(range.getMin(), is(LocalDate.of(2015, APRIL, 1)));
        assertThat(range.getMax(), is(LocalDate.of(2015, MAY, 1)));
        assertThat(histogram.get(range), is(1L));
    }

    @Test
    public void should_create_year_histogram() {
        //given
        final StreamDateHistogramStatistics stats = new StreamDateHistogramStatistics();
        stats.setNumberOfBins(4);

        stats.add(LocalDate.of(2013, JANUARY, 5));
        stats.add(LocalDate.of(2015, JANUARY, 6));
        stats.add(LocalDate.of(2015, MARCH, 15));
        stats.add(LocalDate.of(2013, APRIL, 1));
        stats.add(LocalDate.of(2015, MARCH, 25));
        stats.add(LocalDate.of(2015, JANUARY, 5));

        //when
        final Map<Range<LocalDate>, Long> histogram = stats.getHistogram();

        //then
        assertThat(histogram.size(), is(3));

        final List<Range<LocalDate>> ranges = new ArrayList<>(histogram.keySet());
        Range<LocalDate> range = ranges.get(0);
        assertThat(range.getMin(), is(LocalDate.of(2013, JANUARY, 1)));
        assertThat(range.getMax(), is(LocalDate.of(2014, JANUARY, 1)));
        assertThat(histogram.get(range), is(2L));

        range = ranges.get(1);
        assertThat(range.getMin(), is(LocalDate.of(2014, JANUARY, 1)));
        assertThat(range.getMax(), is(LocalDate.of(2015, JANUARY, 1)));
        assertThat(histogram.get(range), is(0L));

        range = ranges.get(2);
        assertThat(range.getMin(), is(LocalDate.of(2015, JANUARY, 1)));
        assertThat(range.getMax(), is(LocalDate.of(2016, JANUARY, 1)));
        assertThat(histogram.get(range), is(4L));
    }
}
