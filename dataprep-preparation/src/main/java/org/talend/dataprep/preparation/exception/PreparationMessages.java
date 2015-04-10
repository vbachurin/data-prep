package org.talend.dataprep.preparation.exception;

import org.talend.dataprep.exception.Messages;

public enum PreparationMessages implements Messages {
    PREPARATION_DOES_NOT_EXIST,
    UNABLE_TO_SERVE_PREPARATION_CONTENT;

    @Override
    public String getProduct() {
        return "TDP"; //$NON-NLS-1$
    }

    @Override
    public String getGroup() {
        return "PS"; //$NON-NLS-1$
    }
}
