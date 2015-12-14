package org.talend.dataprep.api.dataset.statistics.date;

import org.junit.Test;
import org.talend.dataprep.api.dataset.statistics.Histogram;
import org.talend.dataprep.api.dataset.statistics.HistogramRange;
import org.talend.dataprep.api.dataset.statistics.Range;
import org.talend.dataprep.date.DateManipulator;

import java.time.LocalDateTime;
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

        stats.add(LocalDateTime.of(2015, JANUARY, 5, 5, 6, 7));
        stats.add(LocalDateTime.of(2015, JANUARY, 6, 0, 0, 0));
        stats.add(LocalDateTime.of(2015, JANUARY, 7, 12, 58, 0));
        stats.add(LocalDateTime.of(2015, JANUARY, 6, 2, 4, 45));
        stats.add(LocalDateTime.of(2015, JANUARY, 5, 3, 45, 2));
        stats.add(LocalDateTime.of(2015, JANUARY, 5, 9, 8, 3));

        //when
        final Histogram<LocalDateTime> histogram = stats.getHistogram();

        //then
        assertThat(((DateHistogram)histogram).getPace(), is(DateManipulator.Pace.DAY));
        assertThat(histogram.getItems().size(), is(3));

        HistogramRange<LocalDateTime> histoRange = histogram.getItems().get(0);
        assertThat(histoRange.getRange().getMin(), is(LocalDateTime.of(2015, JANUARY, 5, 0, 0)));
        assertThat(histoRange.getRange().getMax(), is(LocalDateTime.of(2015, JANUARY, 6, 0, 0)));
        assertThat(histoRange.getOccurrences(), is(3L));

        histoRange = histogram.getItems().get(1);
        assertThat(histoRange.getRange().getMin(), is(LocalDateTime.of(2015, JANUARY, 6, 0, 0)));
        assertThat(histoRange.getRange().getMax(), is(LocalDateTime.of(2015, JANUARY, 7, 0, 0)));
        assertThat(histoRange.getOccurrences(), is(2L));

        histoRange = histogram.getItems().get(2);
        assertThat(histoRange.getRange().getMin(), is(LocalDateTime.of(2015, JANUARY, 7, 0, 0)));
        assertThat(histoRange.getRange().getMax(), is(LocalDateTime.of(2015, JANUARY, 8, 0, 0)));
        assertThat(histoRange.getOccurrences(), is(1L));
    }

    @Test
    public void should_create_month_histogram() {
        //given
        final StreamDateHistogramStatistics stats = new StreamDateHistogramStatistics();
        stats.setNumberOfBins(5);

        stats.add(LocalDateTime.of(2015, JANUARY, 5, 2, 4));
        stats.add(LocalDateTime.of(2015, JANUARY, 6, 4, 12));
        stats.add(LocalDateTime.of(2015, MARCH, 15, 16, 5));
        stats.add(LocalDateTime.of(2015, APRIL, 1, 10, 2));
        stats.add(LocalDateTime.of(2015, MARCH, 25, 14, 14));
        stats.add(LocalDateTime.of(2015, JANUARY, 5, 2, 25));
        stats.add(LocalDateTime.of(2015, JANUARY, 18, 4, 4));

        //when
        final Histogram<LocalDateTime> histogram = stats.getHistogram();

        //then
        assertThat(((DateHistogram)histogram).getPace(), is(DateManipulator.Pace.MONTH));
        assertThat(histogram.getItems().size(), is(4));

        HistogramRange<LocalDateTime> histoRange = histogram.getItems().get(0);
        assertThat(histoRange.getRange().getMin(), is(LocalDateTime.of(2015, JANUARY, 1, 0, 0)));
        assertThat(histoRange.getRange().getMax(), is(LocalDateTime.of(2015, FEBRUARY, 1, 0, 0)));
        assertThat(histoRange.getOccurrences(), is(4L));

        histoRange = histogram.getItems().get(1);
        assertThat(histoRange.getRange().getMin(), is(LocalDateTime.of(2015, FEBRUARY, 1, 0, 0)));
        assertThat(histoRange.getRange().getMax(), is(LocalDateTime.of(2015, MARCH, 1, 0, 0)));
        assertThat(histoRange.getOccurrences(), is(0L));

        histoRange = histogram.getItems().get(2);
        assertThat(histoRange.getRange().getMin(), is(LocalDateTime.of(2015, MARCH, 1, 0, 0)));
        assertThat(histoRange.getRange().getMax(), is(LocalDateTime.of(2015, APRIL, 1, 0, 0)));
        assertThat(histoRange.getOccurrences(), is(2L));

        histoRange = histogram.getItems().get(3);
        assertThat(histoRange.getRange().getMin(), is(LocalDateTime.of(2015, APRIL, 1, 0, 0)));
        assertThat(histoRange.getRange().getMax(), is(LocalDateTime.of(2015, MAY, 1, 0, 0)));
        assertThat(histoRange.getOccurrences(), is(1L));
    }

    @Test
    public void should_create_year_histogram() {
        //given
        final StreamDateHistogramStatistics stats = new StreamDateHistogramStatistics();
        stats.setNumberOfBins(4);

        stats.add(LocalDateTime.of(2013, JANUARY, 5, 8, 8));
        stats.add(LocalDateTime.of(2015, JANUARY, 6, 9, 9));
        stats.add(LocalDateTime.of(2015, MARCH, 15, 0, 0));
        stats.add(LocalDateTime.of(2013, APRIL, 1, 12, 12));
        stats.add(LocalDateTime.of(2015, MARCH, 25, 4, 10));
        stats.add(LocalDateTime.of(2015, JANUARY, 5, 7, 9));

        //when
        final Histogram<LocalDateTime> histogram = stats.getHistogram();

        //then
        assertThat(((DateHistogram)histogram).getPace(), is(DateManipulator.Pace.YEAR));
        assertThat(histogram.getItems().size(), is(3));

        HistogramRange<LocalDateTime> histoRange = histogram.getItems().get(0);
        assertThat(histoRange.getRange().getMin(), is(LocalDateTime.of(2013, JANUARY, 1, 0, 0)));
        assertThat(histoRange.getRange().getMax(), is(LocalDateTime.of(2014, JANUARY, 1, 0, 0)));
        assertThat(histoRange.getOccurrences(), is(2L));

        histoRange = histogram.getItems().get(1);
        assertThat(histoRange.getRange().getMin(), is(LocalDateTime.of(2014, JANUARY, 1, 0, 0)));
        assertThat(histoRange.getRange().getMax(), is(LocalDateTime.of(2015, JANUARY, 1, 0, 0)));
        assertThat(histoRange.getOccurrences(), is(0L));

        histoRange = histogram.getItems().get(2);
        assertThat(histoRange.getRange().getMin(), is(LocalDateTime.of(2015, JANUARY, 1, 0, 0)));
        assertThat(histoRange.getRange().getMax(), is(LocalDateTime.of(2016, JANUARY, 1, 0, 0)));
        assertThat(histoRange.getOccurrences(), is(4L));
    }
}
