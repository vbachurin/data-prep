package org.talend.dataprep.api.dataset.statistics;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PatternFrequency implements Serializable {

    /** Serialization UID. */
    private static final long serialVersionUID = 1L;

    @JsonProperty("pattern")
    String pattern;

    @JsonProperty("occurrences")
    long occurrences;

    // Here for JSON deserialization
    public PatternFrequency() {
    }

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

    @Override
    public String toString() {
        return "PatternFrequency{" + "pattern='" + pattern + '\'' + ", occurrences=" + occurrences + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PatternFrequency)) {
            return false;
        }

        PatternFrequency that = (PatternFrequency) o;

        if (occurrences != that.occurrences) {
            return false;
        }
        return pattern.equals(that.pattern);

    }

    @Override
    public int hashCode() {
        int result = pattern.hashCode();
        result = 31 * result + (int) (occurrences ^ (occurrences >>> 32));
        return result;
    }
}
