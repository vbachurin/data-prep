package org.talend.dataprep.api.dataset.statistics;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.talend.dataprep.api.dataset.statistics.date.DateHistogramRange;

import java.io.Serializable;

/**
 * Histogram item. It represents the range and its occurrences
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = NumberHistogramRange.class, name = NumberHistogramRange.TYPE),
        @JsonSubTypes.Type(value = DateHistogramRange.class, name = DateHistogramRange.TYPE)
})
public class HistogramRange<T> implements Serializable {
    /**
     * The number of element in the curent range
     */
    @JsonProperty("occurrences")
    long occurrences = 0;

    /**
     * The range of this part of the histogram
     */
    @JsonProperty("range")
    Range<T> range = new Range<>();

    /**
     * Number of element getter
     * @return The number of elements
     */
    public long getOccurrences() {
        return occurrences;
    }

    /**
     * Number of elements setter
     * @param occurrences The new number of occurrences
     */
    public void setOccurrences(long occurrences) {
        this.occurrences = occurrences;
    }

    /**
     * Range getter
     * @return The range
     */
    public Range<T> getRange() {
        return range;
    }

    /**
     * Range setter
     * @param range The new range
     */
    public void setRange(Range<T> range) {
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
        final HistogramRange that = (HistogramRange) o;
        return occurrences == that.occurrences && range.equals(that.range);
    }

    @Override
    public int hashCode() {
        int result = (int) (occurrences ^ (occurrences >>> 32));
        result = 31 * result + range.hashCode();
        return result;
    }
}
