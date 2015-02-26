package org.talend.dataprep.exception;

class InternalException extends TDPException {

    public InternalException(Messages message) {
        super(message);
    }

    public InternalException(Messages message, Throwable cause) {
        super(message, cause);
    }
}
