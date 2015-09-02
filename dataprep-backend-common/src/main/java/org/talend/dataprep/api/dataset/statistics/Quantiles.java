package org.talend.dataprep.api.dataset.statistics;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Quantiles {

    @JsonProperty("median")
    String median;

    @JsonProperty("lowerQuantile")
    String lowerQuantile;

    @JsonProperty("upperQuantile")
    String upperQuantile;

    public String getMedian() {
        return median;
    }

    public void setMedian(String median) {
        this.median = median;
    }

    public String getLowerQuantile() {
        return lowerQuantile;
    }

    public void setLowerQuantile(String lowerQuantile) {
        this.lowerQuantile = lowerQuantile;
    }

    public String getUpperQuantile() {
        return upperQuantile;
    }

    public void setUpperQuantile(String upperQuantile) {
        this.upperQuantile = upperQuantile;
    }
}
