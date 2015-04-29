package org.talend.dataprep.preparation.exception;

import org.talend.dataprep.exception.ErrorCode;

public enum PreparationErrorCodes implements ErrorCode {
    PREPARATION_DOES_NOT_EXIST, // 404 ?
    UNABLE_TO_SERVE_PREPARATION_CONTENT; //400

    @Override
    public String getProduct() {
        return "TDP"; //$NON-NLS-1$
    }

    @Override
    public String getGroup() {
        return "PS"; //$NON-NLS-1$
    }

    //TODO move this to codes
    @Override
    public int getHttpStatus() {return 400;}
}
