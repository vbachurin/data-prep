package org.talend.dataprep.api.dataset.statistics;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Range implements Serializable {

    @JsonProperty("min")
    double min;

    @JsonProperty("max")
    double max;

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
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

        Range range = (Range) o;

        if (Double.compare(range.min, min) != 0) {
            return false;
        }
        return Double.compare(range.max, max) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(min);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(max);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
