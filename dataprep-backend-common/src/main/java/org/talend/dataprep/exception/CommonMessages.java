package org.talend.dataprep.exception;

public enum CommonMessages implements Messages {
    UNEXPECTED_EXCEPTION, UNABLE_TO_PARSE_JSON, UNABLE_TO_SERIALIZE_TO_JSON, UNABLE_TO_COMPUTE_ID, UNABLE_TO_PRINT_PREPARATION, UNABLE_TO_READ_CONTENT, UNABLE_TO_PARSE_ACTIONS;

    @Override
    public String getProduct() {
        return "TDP"; //$NON-NLS-1$
    }

    @Override
    public String getGroup() {
        return "ALL"; //$NON-NLS-1$
    }
}
