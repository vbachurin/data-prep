package org.talend.dataprep.api.dataset.statistics;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Class that represents a range [min, max[
 * @param <T> The type of bound values
 */
public class Range<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * The min range value
     */
    @JsonProperty("min")
    T min;

    /**
     * The max range value
     */
    @JsonProperty("max")
    T max;

    /**
     * Constructor
     */
    public Range() {
    }

    /**
     * Constructor
     * @param min The minimum value
     * @param max The maximum value
     */
    public Range(T min, T max) {
        this.min = min;
        this.max = max;
    }

    /**
     * Minimum value getter
     * @return The minimum value
     */
    public T getMin() {
        return min;
    }

    /**
     * Minimum value setter
     * @param min The new minimum value
     */
    public void setMin(T min) {
        this.min = min;
    }

    /**
     * Maximum value getter
     * @return The maximum value
     */
    public T getMax() {
        return max;
    }

    /**
     * Maximum value setter
     * @param max The new maximum value
     */
    public void setMax(T max) {
        this.max = max;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Range)) {
            return false;
        }

        final Range range = (Range) o;
        return range.min.equals(min) && range.max.equals(max);
    }

    @Override
    public int hashCode() {
        long minHashCode = min.hashCode();
        long maxHashCode = max.hashCode();

        int result = (int) (minHashCode ^ (minHashCode >>> 32));
        result = 31 * result + (int) (maxHashCode ^ (maxHashCode >>> 32));
        return result;
    }
}
