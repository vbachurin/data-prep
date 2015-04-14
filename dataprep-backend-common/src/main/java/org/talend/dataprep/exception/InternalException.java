package org.talend.dataprep.exception;

class InternalException extends TDPException {

    public InternalException(Messages code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
