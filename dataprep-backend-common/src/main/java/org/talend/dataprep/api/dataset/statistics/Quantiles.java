package org.talend.dataprep.api.dataset.statistics;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Quantiles {

    @JsonProperty("median")
    String median;

    @JsonProperty("lowerQuantile")
    String lowerQuantile;

    @JsonProperty("upperQuantile")
    String upperQuantile;

}
