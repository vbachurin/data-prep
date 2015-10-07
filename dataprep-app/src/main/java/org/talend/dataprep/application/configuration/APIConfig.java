package org.talend.dataprep.application.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

public class APIConfig {

    @JsonProperty("serverUrl")
    private String serverUrl;

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }
}
