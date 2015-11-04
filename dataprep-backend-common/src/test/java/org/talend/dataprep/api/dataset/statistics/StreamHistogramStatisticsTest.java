package org.talend.dataprep.api.dataset.statistics;

import org.junit.Test;
import java.lang.IllegalArgumentException;
import java.util.Collection;
import org.talend.dataquality.statistics.numeric.histogram.Range;

import static org.junit.Assert.assertEquals;

public class StreamHistogramStatisticsTest {

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionWhenNegativeOrZeroNumberOfBins() {
        // given
        final StreamHistogramStatistics histogram = new StreamHistogramStatistics();
        histogram.setParameters(0);
    }

    @Test
    public void shouldBConsistentWhenNoValueAdded() throws Exception {
        // given
        final StreamHistogramStatistics histogram = new StreamHistogramStatistics();
        histogram.setParameters(32);
        //expected
        assertEquals(0,histogram.getNumberOfValues());
        assertEquals(0, histogram.getMean(), 0);
        assertEquals(0, histogram.getMin(), 0);
        assertEquals(0, histogram.getMax(), 0);
        assertEquals(32, histogram.getNumberOfBins());
    }

    @Test
    public void shouldBeConsistentWhenOneValueAdded() throws Exception {
        // given
        final StreamHistogramStatistics histogram = new StreamHistogramStatistics();
        histogram.setParameters(16);

        //when
        histogram.add(1);
        //expected
        assertEquals(1, histogram.getNumberOfValues());
        assertEquals(1, histogram.getMean(), 0);
        assertEquals(1, histogram.getMin(), 0);
        assertEquals(1, histogram.getMax(), 0);
        assertEquals(16, histogram.getNumberOfBins());
    }

    @Test
    public void shouldBeConsistentWhenAHundredValuesFromOneToOneHundredAreAdded() throws Exception {
        // given
        final StreamHistogramStatistics histogram = new StreamHistogramStatistics();
        histogram.setParameters(16);
        //when
        for (int i = 1; i <=100; i++){
            histogram.add(i);
        }
        //expected
        assertEquals(histogram.getNumberOfValues(),100);
        assertEquals(histogram.getMean(),50.5,0);
        assertEquals(histogram.getMin(), 1, 0);
        assertEquals(histogram.getMax(), 100, 0);
        assertEquals(histogram.getNumberOfBins(), 16);
        //assertEquals(histogram.getHistogram().size(), 16);

        Collection<Long> counts = histogram.getHistogram().values();
        long sum = 0;
        for (Long l: counts){
            sum += l;
        }
        assertEquals(sum, histogram.getNumberOfValues() );
    }

    @Test
    public void shouldBeConsistentWhenAHundredValuesFromOneHundredToOneAreAdded() throws Exception {
        // given
        final StreamHistogramStatistics histogram = new StreamHistogramStatistics();
        histogram.setParameters(32);
        for (int i = 100; i >=1; i--){
            histogram.add(i);
        }
        //expected
        assertEquals(histogram.getNumberOfValues(),100);
        assertEquals(histogram.getMean(),50.5,0);
        assertEquals(histogram.getMin(), 1, 0);
        assertEquals(histogram.getMax(), 100, 0);
        assertEquals(histogram.getNumberOfBins(), 32);
        //assertEquals(histogram.getHistogram().size(), 32);

        Collection<Long> counts = histogram.getHistogram().values();
        long sum = 0;
        for (Long l: counts){
            sum += l;
        }
        assertEquals(sum, histogram.getNumberOfValues() );
    }

    @Test
    public void shouldHaveConsistentRangesWhenAThousandValuesAreAdded() throws Exception {
        // given
        final StreamHistogramStatistics histogram = new StreamHistogramStatistics();
        histogram.setParameters(32);
        for (int i = 1000; i >=1; i--){
            histogram.add(i);
        }
        //expected
        Range previousRange = null;
        for ( Range range: histogram.getHistogram().keySet()){
            if (previousRange != null){
                assertEquals(previousRange.getUpper(), range.getLower(), 0);
            }
            previousRange = range;
        }
    }

    @Test
    public void shouldHaveSameMinWhenMinIsAdded() throws Exception {
        // given
        final StreamHistogramStatistics histogram = new StreamHistogramStatistics();
        histogram.setParameters(4);
        histogram.add(1);
        double min = histogram.getMin();
        //when
        histogram.add(1);

        //expected
        assertEquals(min, histogram.getMin(), 0);
    }

    @Test
    public void shouldHaveSameMaxWhenMaxIsAdded() throws Exception {
        // given
        final StreamHistogramStatistics histogram = new StreamHistogramStatistics();
        histogram.setParameters(4);
        histogram.add(1);
        double max = histogram.getMin();
        //when
        histogram.add(1);

        //expected
        assertEquals(max, histogram.getMin(), 0);
    }

    @Test
    public void shouldHaveMinMaxAndMeanWhenAValueIsAdded() throws Exception {
        // given
        final StreamHistogramStatistics histogram = new StreamHistogramStatistics();
        histogram.setParameters(4);
        histogram.add(10);

        //expected
        assertEquals(10, histogram.getMin(), 0);
        assertEquals(10, histogram.getMax(), 0);
        assertEquals(10, histogram.getMean(), 0);
    }

}
