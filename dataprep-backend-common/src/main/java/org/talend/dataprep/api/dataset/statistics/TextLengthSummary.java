package org.talend.dataprep.api.dataset.statistics;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TextLengthSummary {

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
}
