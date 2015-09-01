package org.talend.dataprep.api.dataset.statistics;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TextLengthSummary {

    @JsonProperty("minimalLength")
    float minimalLength = 0f;

    @JsonProperty("maximalLength")
    float maximalLength = 0f;

    @JsonProperty("averageLength")
    float averageLength = 0f;

}
