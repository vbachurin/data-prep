package org.talend.dataprep.transformation.exception;

import org.talend.dataprep.exception.ErrorCode;

public enum TransformationErrorCodes implements ErrorCode {

    UNABLE_TO_PARSE_JSON, // 400 or 500 ?
    UNEXPECTED_EXCEPTION, // 500
    UNABLE_TO_COMPUTE_DATASET_ACTIONS,
    UNABLE_TO_WRITE_JSON; // 500

    @Override
    public String getGroup() {
        return "TS"; //$NON-NLS-1$
    }

    @Override
    public String getProduct() {
        return "TDP"; //$NON-NLS-1$
    }

    //TODO move this to error codes
    @Override
    public int getHttpStatus() {
        return 400;
    }
}
