package org.talend.dataprep.api.dataset.statistics;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("statistics")
public class Statistics {

    @JsonProperty("count")
    long count;

    @JsonProperty("valid")
    long valid;

    @JsonProperty("invalid")
    long invalid;

    @JsonProperty("empty")
    long empty;

    @JsonProperty("max")
    String max = "NaN";

    @JsonProperty("min")
    String min = "NaN";

    @JsonProperty("mean")
    String mean = "NaN";

    @JsonProperty("variance")
    String variance = "NaN";

    @JsonProperty("duplicateCount")
    long duplicateCount = 0;

    @JsonProperty("distinctCount")
    long distinctCount = 0;

    @JsonProperty("frequencyTable")
    List<DataFrequency> dataFrequencies;

    @JsonProperty("patternFrequencyTable")
    List<PatternFrequency> patternFrequencies;

    @JsonProperty("quantiles")
    Quantiles quantiles;

    @JsonProperty("histogram")
    Histogram histogram;

    @JsonProperty("textLengthSummary")
    TextLengthSummary textLengthSummary;

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public long getValid() {
        return valid;
    }

    public void setValid(long valid) {
        this.valid = valid;
    }

    public long getInvalid() {
        return invalid;
    }

    public void setInvalid(long invalid) {
        this.invalid = invalid;
    }

    public long getEmpty() {
        return empty;
    }

    public void setEmpty(long empty) {
        this.empty = empty;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getMean() {
        return mean;
    }

    public void setMean(String mean) {
        this.mean = mean;
    }

    public String getVariance() {
        return variance;
    }

    public void setVariance(String variance) {
        this.variance = variance;
    }

    public long getDuplicateCount() {
        return duplicateCount;
    }

    public void setDuplicateCount(long duplicateCount) {
        this.duplicateCount = duplicateCount;
    }

    public long getDistinctCount() {
        return distinctCount;
    }

    public void setDistinctCount(long distinctCount) {
        this.distinctCount = distinctCount;
    }

    public List<DataFrequency> getDataFrequencies() {
        return dataFrequencies;
    }

    public void setDataFrequencies(List<DataFrequency> dataFrequencies) {
        this.dataFrequencies = dataFrequencies;
    }

    public List<PatternFrequency> getPatternFrequencies() {
        return patternFrequencies;
    }

    public void setPatternFrequencies(List<PatternFrequency> patternFrequencies) {
        this.patternFrequencies = patternFrequencies;
    }

    public Quantiles getQuantiles() {
        return quantiles;
    }

    public void setQuantiles(Quantiles quantiles) {
        this.quantiles = quantiles;
    }

    public Histogram getHistogram() {
        return histogram;
    }

    public void setHistogram(Histogram histogram) {
        this.histogram = histogram;
    }

    public TextLengthSummary getTextLengthSummary() {
        return textLengthSummary;
    }

    public void setTextLengthSummary(TextLengthSummary textLengthSummary) {
        this.textLengthSummary = textLengthSummary;
    }
}
