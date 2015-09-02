package org.talend.dataprep.api.dataset.statistics;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HistogramRange {

    @JsonProperty("occurrences")
    long occurrences = 0;

    @JsonProperty("range")
    Range range = new Range();

    public long getOccurrences() {
        return occurrences;
    }

    public void setOccurrences(long occurrences) {
        this.occurrences = occurrences;
    }

    public Range getRange() {
        return range;
    }

    public void setRange(Range range) {
        this.range = range;
    }
}
