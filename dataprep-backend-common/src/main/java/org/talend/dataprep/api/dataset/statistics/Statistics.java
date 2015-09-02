package org.talend.dataprep.api.dataset.statistics;

import java.util.LinkedList;
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
    double max = 0;

    @JsonProperty("min")
    double min = 0;

    @JsonProperty("mean")
    double mean = 0;

    @JsonProperty("variance")
    double variance = 0;

    @JsonProperty("duplicateCount")
    long duplicateCount = 0;

    @JsonProperty("distinctCount")
    long distinctCount = 0;

    @JsonProperty("frequencyTable")
    List<DataFrequency> dataFrequencies = new LinkedList<>();

    @JsonProperty("patternFrequencyTable")
    List<PatternFrequency> patternFrequencies = new LinkedList<>();

    @JsonProperty("quantiles")
    Quantiles quantiles = new Quantiles();

    @JsonProperty("histogram")
    List<HistogramRange> histogram = new LinkedList<>();

    @JsonProperty("textLengthSummary")
    TextLengthSummary textLengthSummary = new TextLengthSummary();

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

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMean() {
        return mean;
    }

    public void setMean(double mean) {
        this.mean = mean;
    }

    public double getVariance() {
        return variance;
    }

    public void setVariance(double variance) {
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

    public List<HistogramRange> getHistogram() {
        return histogram;
    }

    public void setHistogram(List<HistogramRange> histogram) {
        this.histogram = histogram;
    }

    public TextLengthSummary getTextLengthSummary() {
        return textLengthSummary;
    }

    public void setTextLengthSummary(TextLengthSummary textLengthSummary) {
        this.textLengthSummary = textLengthSummary;
    }
}
