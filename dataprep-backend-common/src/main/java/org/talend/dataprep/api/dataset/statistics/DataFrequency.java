package org.talend.dataprep.api.dataset.statistics;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DataFrequency implements Serializable {

    @JsonProperty("data")
    String data;

    @JsonProperty("occurrences")
    long occurrences;

    // Here for JSON deserialization
    public DataFrequency() {
    }

    public DataFrequency(String data, long occurrences) {
        this.data = data;
        this.occurrences = occurrences;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataFrequency)) return false;

        DataFrequency that = (DataFrequency) o;

        if (occurrences != that.occurrences) return false;
        return data.equals(that.data);

    }

    @Override
    public int hashCode() {
        int result = data.hashCode();
        result = 31 * result + (int) (occurrences ^ (occurrences >>> 32));
        return result;
    }
}
