package org.talend.dataprep.api.dataset.statistics;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HistogramRange implements Serializable {

    /** Serialization UID. */
    private static final long serialVersionUID = 1L;

    @JsonProperty("occurrences")
    long occurrences = 0;

    @JsonProperty("range")
    Range range = new Range();

    public long getOccurrences() {
        return occurrences;
    }

    public void setOccurrences(long occurrences) {
        this.occurrences = occurrences;
    }

    public Range getRange() {
        return range;
    }

    public void setRange(Range range) {
        this.range = range;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HistogramRange)) {
            return false;
        }
        HistogramRange that = (HistogramRange) o;
        if (occurrences != that.occurrences) {
            return false;
        }
        return range.equals(that.range);

    }

    @Override
    public int hashCode() {
        int result = (int) (occurrences ^ (occurrences >>> 32));
        result = 31 * result + range.hashCode();
        return result;
    }
}
