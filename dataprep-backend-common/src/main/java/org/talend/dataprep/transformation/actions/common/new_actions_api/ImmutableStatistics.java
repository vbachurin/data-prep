package org.talend.dataprep.transformation.actions.common.new_actions_api;

import org.talend.dataprep.api.dataset.statistics.*;

import java.util.List;

/**
 * Immutable view of Statistics. Not perfect as DataFrequency, PatternFrequency, Quantiles, Histogram and TextLengthSummary are
 * not immutables.
 */
public class ImmutableStatistics {

    private Statistics delegate;

    public ImmutableStatistics(Statistics delegate) {
        this.delegate = delegate;
    }

    public long getCount() {
        return delegate.getCount();
    }

    public long getValid() {
        return delegate.getValid();
    }

    public long getInvalid() {
        return delegate.getInvalid();
    }

    public long getEmpty() {
        return delegate.getEmpty();
    }

    public double getMax() {
        return delegate.getMax();
    }

    public double getMin() {
        return delegate.getMin();
    }

    public double getMean() {
        return delegate.getMean();
    }

    public double getVariance() {
        return delegate.getVariance();
    }

    public long getDuplicateCount() {
        return delegate.getDuplicateCount();
    }

    public long getDistinctCount() {
        return delegate.getDistinctCount();
    }

    public List<DataFrequency> getDataFrequencies() {
        return delegate.getDataFrequencies();
    }

    public List<PatternFrequency> getPatternFrequencies() {
        return delegate.getPatternFrequencies();
    }

    public Quantiles getQuantiles() {
        return delegate.getQuantiles();
    }

    public Histogram getHistogram() {
        return delegate.getHistogram();
    }

    public TextLengthSummary getTextLengthSummary() {
        return delegate.getTextLengthSummary();
    }

}
