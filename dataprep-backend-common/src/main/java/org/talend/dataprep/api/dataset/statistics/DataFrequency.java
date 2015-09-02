package org.talend.dataprep.api.dataset.statistics;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DataFrequency {

    @JsonProperty("data")
    String data;

    @JsonProperty("occurrences")
    long occurrences;

    public DataFrequency(String data, long occurrences) {
        this.data = data;
        this.occurrences = occurrences;
    }
}
