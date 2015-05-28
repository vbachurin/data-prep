package org.talend.dataprep.api.type;

public enum ExportType {
    CSV("text/csv", ".csv"), XLS("application/vnd.ms-excel", ".xls"), TABLEAU("application/tde", ".tde");

    private final String mimeType;
    private final String entension;

    ExportType(final String mimeType, String entension) {
        this.mimeType = mimeType;
        this.entension = entension;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getEntension() {
        return entension;
    }
}
