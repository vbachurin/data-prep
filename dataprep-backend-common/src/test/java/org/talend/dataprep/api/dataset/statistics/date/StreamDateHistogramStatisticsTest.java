package org.talend.dataprep.api.dataset.statistics.date;

import org.junit.Test;
import org.talend.dataprep.api.dataset.statistics.Histogram;
import org.talend.dataprep.api.dataset.statistics.HistogramRange;
import org.talend.dataprep.api.dataset.statistics.Range;
import org.talend.dataprep.date.DateManipulator;

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
        final Histogram<LocalDate> histogram = stats.getHistogram();

        //then
        assertThat(((DateHistogram)histogram).getPace(), is(DateManipulator.Pace.DAY));
        assertThat(histogram.getItems().size(), is(3));

        HistogramRange<LocalDate> histoRange = histogram.getItems().get(0);
        assertThat(histoRange.getRange().getMin(), is(LocalDate.of(2015, JANUARY, 5)));
        assertThat(histoRange.getRange().getMax(), is(LocalDate.of(2015, JANUARY, 6)));
        assertThat(histoRange.getOccurrences(), is(3L));

        histoRange = histogram.getItems().get(1);
        assertThat(histoRange.getRange().getMin(), is(LocalDate.of(2015, JANUARY, 6)));
        assertThat(histoRange.getRange().getMax(), is(LocalDate.of(2015, JANUARY, 7)));
        assertThat(histoRange.getOccurrences(), is(2L));

        histoRange = histogram.getItems().get(2);
        assertThat(histoRange.getRange().getMin(), is(LocalDate.of(2015, JANUARY, 7)));
        assertThat(histoRange.getRange().getMax(), is(LocalDate.of(2015, JANUARY, 8)));
        assertThat(histoRange.getOccurrences(), is(1L));
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
        final Histogram<LocalDate> histogram = stats.getHistogram();

        //then
        assertThat(((DateHistogram)histogram).getPace(), is(DateManipulator.Pace.MONTH));
        assertThat(histogram.getItems().size(), is(4));

        HistogramRange<LocalDate> histoRange = histogram.getItems().get(0);
        assertThat(histoRange.getRange().getMin(), is(LocalDate.of(2015, JANUARY, 1)));
        assertThat(histoRange.getRange().getMax(), is(LocalDate.of(2015, FEBRUARY, 1)));
        assertThat(histoRange.getOccurrences(), is(4L));

        histoRange = histogram.getItems().get(1);
        assertThat(histoRange.getRange().getMin(), is(LocalDate.of(2015, FEBRUARY, 1)));
        assertThat(histoRange.getRange().getMax(), is(LocalDate.of(2015, MARCH, 1)));
        assertThat(histoRange.getOccurrences(), is(0L));

        histoRange = histogram.getItems().get(2);
        assertThat(histoRange.getRange().getMin(), is(LocalDate.of(2015, MARCH, 1)));
        assertThat(histoRange.getRange().getMax(), is(LocalDate.of(2015, APRIL, 1)));
        assertThat(histoRange.getOccurrences(), is(2L));

        histoRange = histogram.getItems().get(3);
        assertThat(histoRange.getRange().getMin(), is(LocalDate.of(2015, APRIL, 1)));
        assertThat(histoRange.getRange().getMax(), is(LocalDate.of(2015, MAY, 1)));
        assertThat(histoRange.getOccurrences(), is(1L));
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
        final Histogram<LocalDate> histogram = stats.getHistogram();

        //then
        assertThat(((DateHistogram)histogram).getPace(), is(DateManipulator.Pace.YEAR));
        assertThat(histogram.getItems().size(), is(3));

        HistogramRange<LocalDate> histoRange = histogram.getItems().get(0);
        assertThat(histoRange.getRange().getMin(), is(LocalDate.of(2013, JANUARY, 1)));
        assertThat(histoRange.getRange().getMax(), is(LocalDate.of(2014, JANUARY, 1)));
        assertThat(histoRange.getOccurrences(), is(2L));

        histoRange = histogram.getItems().get(1);
        assertThat(histoRange.getRange().getMin(), is(LocalDate.of(2014, JANUARY, 1)));
        assertThat(histoRange.getRange().getMax(), is(LocalDate.of(2015, JANUARY, 1)));
        assertThat(histoRange.getOccurrences(), is(0L));

        histoRange = histogram.getItems().get(2);
        assertThat(histoRange.getRange().getMin(), is(LocalDate.of(2015, JANUARY, 1)));
        assertThat(histoRange.getRange().getMax(), is(LocalDate.of(2016, JANUARY, 1)));
        assertThat(histoRange.getOccurrences(), is(4L));
    }
}
