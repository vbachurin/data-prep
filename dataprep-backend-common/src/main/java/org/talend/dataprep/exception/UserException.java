package org.talend.dataprep.exception;

class UserException extends TDPException {

    public UserException(Messages code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
