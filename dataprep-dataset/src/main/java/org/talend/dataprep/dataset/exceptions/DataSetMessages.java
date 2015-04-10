package org.talend.dataprep.dataset.exceptions;

import org.talend.dataprep.exception.Messages;

public enum DataSetMessages implements Messages {
    UNEXPECTED_IO_EXCEPTION, UNABLE_TO_READ_DATASET_CONTENT, UNEXPECTED_JMS_EXCEPTION, UNABLE_TO_CLEAR_DATASETS, UNABLE_TO_DELETE_DATASET, UNABLE_TO_CONNECT_TO_HDFS, UNABLE_TO_STORE_DATASET_CONTENT;

    @Override
    public String getProduct() {
        return "TDP"; //$NON-NLS-1$
    }

    @Override
    public String getGroup() {
        return "DSS"; //$NON-NLS-1$
    }
}
