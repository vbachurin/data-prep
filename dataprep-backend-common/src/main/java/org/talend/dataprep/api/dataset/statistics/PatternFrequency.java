package org.talend.dataprep.api.dataset.statistics;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PatternFrequency {

    @JsonProperty("pattern")
    String pattern;

    @JsonProperty("occurrences")
    long occurrences;

    public PatternFrequency(String pattern, long occurrences) {
        this.pattern = pattern;
        this.occurrences = occurrences;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public long getOccurrences() {
        return occurrences;
    }

    public void setOccurrences(long occurrences) {
        this.occurrences = occurrences;
    }
}
