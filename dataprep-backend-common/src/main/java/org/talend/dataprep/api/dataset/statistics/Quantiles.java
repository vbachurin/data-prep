package org.talend.dataprep.api.dataset.statistics;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Quantiles {

    @JsonProperty("median")
    double median = Double.NaN;

    @JsonProperty("lowerQuantile")
    double lowerQuantile = Double.NaN;

    @JsonProperty("upperQuantile")
    double upperQuantile = Double.NaN;

    public double getMedian() {
        return median;
    }

    public void setMedian(double median) {
        this.median = median;
    }

    public double getLowerQuantile() {
        return lowerQuantile;
    }

    public void setLowerQuantile(double lowerQuantile) {
        this.lowerQuantile = lowerQuantile;
    }

    public double getUpperQuantile() {
        return upperQuantile;
    }

    public void setUpperQuantile(double upperQuantile) {
        this.upperQuantile = upperQuantile;
    }
}
