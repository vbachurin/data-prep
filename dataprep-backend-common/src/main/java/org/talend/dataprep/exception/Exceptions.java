package org.talend.dataprep.exception;

public class Exceptions {

    private Exceptions() {
    }

    public static TDPException Internal(Messages message) {
        return new InternalException(message);
    }

    public static TDPException Internal(Messages message, Throwable cause) {
        if (cause instanceof UserException) {
            return (TDPException) cause;
        }
        return new InternalException(message, cause);
    }

    public static TDPException User(Messages message) {
        return new UserException(message);
    }

    public static TDPException User(Messages message, Throwable cause) {
        return new UserException(message, cause);
    }
}
