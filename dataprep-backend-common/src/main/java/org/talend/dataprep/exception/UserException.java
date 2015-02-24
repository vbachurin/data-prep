package org.talend.dataprep.exception;

class UserException extends TDPException {
    public UserException(Messages message) {
        super(message);
    }

    public UserException(Messages message, Throwable cause) {
        super(message, cause);
    }
}
