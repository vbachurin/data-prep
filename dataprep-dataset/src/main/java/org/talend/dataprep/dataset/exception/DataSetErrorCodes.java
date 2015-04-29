package org.talend.dataprep.dataset.exception;

import org.talend.dataprep.exception.ErrorCode;

public enum DataSetErrorCodes implements ErrorCode {
    UNEXPECTED_IO_EXCEPTION, // 500
    UNABLE_TO_READ_DATASET_CONTENT, //500
    UNEXPECTED_JMS_EXCEPTION, //500
    UNABLE_TO_CLEAR_DATASETS, //500
    UNABLE_TO_DELETE_DATASET, //500
    UNABLE_TO_CONNECT_TO_HDFS, //500
    UNABLE_TO_STORE_DATASET_CONTENT, //500
    UNABLE_TO_ANALYZE_COLUMN_TYPES; //500

    @Override
    public String getProduct() {
        return "TDP"; //$NON-NLS-1$
    }

    @Override
    public String getGroup() {
        return "DSS"; //$NON-NLS-1$
    }

    //TODO move this to codes
    @Override
    public int getHttpStatus() {return 400;}
}
