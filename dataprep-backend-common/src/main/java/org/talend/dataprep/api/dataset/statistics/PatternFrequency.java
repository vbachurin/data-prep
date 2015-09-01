package org.talend.dataprep.api.dataset.statistics;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PatternFrequency {

    @JsonProperty("pattern")
    String pattern;

    @JsonProperty("occurrences")
    int occurrences;

    public PatternFrequency(String pattern, int occurrences) {
        this.pattern = pattern;
        this.occurrences = occurrences;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public int getOccurrences() {
        return occurrences;
    }

    public void setOccurrences(int occurrences) {
        this.occurrences = occurrences;
    }
}
