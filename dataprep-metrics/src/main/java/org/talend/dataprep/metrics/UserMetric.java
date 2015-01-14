package org.talend.dataprep.metrics;

import org.springframework.boot.actuate.metrics.Metric;

public class UserMetric<T extends Number> extends Metric<T> {

    private String user;

    private String organization;

    private String remoteAddress;

    private String sessionId;

    public UserMetric(String name, T value) {
        super(name, value);
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
