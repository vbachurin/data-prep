package org.talend.dataprep.transformation.exception;

public enum Messages implements org.talend.dataprep.exception.Messages {

    UNABLE_TO_PARSE_JSON, UNEXPECTED_EXCEPTION, UNABLE_TO_COMPUTE_DATASET_ACTIONS;

    @Override
    public String getGroup() {
        return "TS"; //$NON-NLS-1$
    }

    @Override
    public String getProduct() {
        return "TDP"; //$NON-NLS-1$
    }
}
