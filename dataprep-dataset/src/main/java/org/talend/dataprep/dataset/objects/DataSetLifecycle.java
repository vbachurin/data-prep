package org.talend.dataprep.dataset.objects;

public class DataSetLifecycle {

    private boolean contentAnalyzed;

    private boolean schemaAnalyzed;

    private boolean qualityAnalyzed;

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

    public void qualityAnalyzed(boolean qualityAnalyzed) {
        this.qualityAnalyzed = qualityAnalyzed;
    }

    public boolean qualityAnalyzed() {
        return qualityAnalyzed;
    }
}
