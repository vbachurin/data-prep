package org.talend.dataprep.api.dataset.statistics;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TextLengthSummary implements Serializable {

    @JsonProperty("minimalLength")
    double minimalLength = Double.NaN;

    @JsonProperty("maximalLength")
    double maximalLength = Double.NaN;

    @JsonProperty("averageLength")
    double averageLength = Double.NaN;

    public double getMinimalLength() {
        return minimalLength;
    }

    public void setMinimalLength(double minimalLength) {
        this.minimalLength = minimalLength;
    }

    public double getMaximalLength() {
        return maximalLength;
    }

    public void setMaximalLength(double maximalLength) {
        this.maximalLength = maximalLength;
    }

    public double getAverageLength() {
        return averageLength;
    }

    public void setAverageLength(double averageLength) {
        this.averageLength = averageLength;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TextLengthSummary)) {
            return false;
        }

        TextLengthSummary that = (TextLengthSummary) o;

        if (Double.compare(that.minimalLength, minimalLength) != 0) {
            return false;
        }
        if (Double.compare(that.maximalLength, maximalLength) != 0) {
            return false;
        }
        return Double.compare(that.averageLength, averageLength) == 0;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(minimalLength);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(maximalLength);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(averageLength);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
