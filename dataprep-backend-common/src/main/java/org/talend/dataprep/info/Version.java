package org.talend.dataprep.info;

public class Version {
    private String versionId;

    private String buildId;

    private String serviceName;

    public Version() {
    }

    public Version(String versionId, String buildId, String serviceName) {
        this.versionId = versionId;
        this.buildId = buildId;
        this.serviceName = serviceName;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public String getBuildId() {
        return buildId;
    }

    public void setBuildId(String buildId) {
        this.buildId = buildId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
