package org.talend.dataprep.dataset.objects;

public class DataSetLifecycle {

    private boolean contentAnalyzed;

    private boolean schemaAnalyzed;

    public void contentIndexed(boolean contentAnalyzed) {
        this.contentAnalyzed = contentAnalyzed;
    }

    public boolean contentIndexed() {
        return contentAnalyzed;
    }

    public void schemaAnalyzed(boolean schemaAnalyzed) {
        this.schemaAnalyzed = schemaAnalyzed;
    }

    public boolean schemaAnalyzed() {
        return schemaAnalyzed;
    }
}
