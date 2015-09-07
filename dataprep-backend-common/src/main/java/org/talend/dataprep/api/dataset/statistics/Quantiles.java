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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Quantiles)) return false;

        Quantiles quantiles = (Quantiles) o;

        if (Double.compare(quantiles.median, median) != 0) return false;
        if (Double.compare(quantiles.lowerQuantile, lowerQuantile) != 0) return false;
        return Double.compare(quantiles.upperQuantile, upperQuantile) == 0;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(median);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(lowerQuantile);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(upperQuantile);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
