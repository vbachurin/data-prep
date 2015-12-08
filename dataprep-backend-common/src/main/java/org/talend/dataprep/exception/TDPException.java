package org.talend.dataprep.exception;

import org.talend.daikon.exception.ExceptionContext;
import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.daikon.exception.error.ErrorCode;
import org.talend.daikon.exception.json.JsonErrorCode;

/**
 * Class for all business (TDP) exception.
 */
public class TDPException extends TalendRuntimeException {

    private static final long serialVersionUID = -51732176302413600L;

    /**
     * this field if set to <code>true</code> will prevent {@link TDPExceptionController} to log a stack trace
     */
    private boolean error = false;

    /**
     * Full constructor.
     *
     * @param code the error code that holds all the .
     * @param cause the root cause of this error.
     * @param context the context of the error when it occurred (used to detail the user error message in frontend).
     */
    public TDPException(ErrorCode code, Throwable cause, ExceptionContext context) {
        super(code, cause, context);
    }

    /**
     * Lightweight constructor without context.
     *
     * @param code the error code that holds all the .
     * @param cause the root cause of this error.
     */
    public TDPException(ErrorCode code, Throwable cause) {
        super(code, cause, null);
    }

    /**
     * Lightweight constructor without a cause.
     *
     * @param code the error code that holds all the .
     * @param context the exception context.
     */
    public TDPException(ErrorCode code, ExceptionContext context) {
        super(code, null, context);
    }

    /**
     * Lightweight constructor without a cause.
     *
     * @param code the error code that holds all the .
     * @param context the exception context.
     */
    public TDPException(ErrorCode code, ExceptionContext context, boolean error) {
        super(code, null, context);
        this.error = true;
    }    
    
    /**
     * Basic constructor from a JSON error code.
     *
     * @param code an error code serialized to JSON.
     */
    public TDPException(JsonErrorCode code) {
        super(code, ExceptionContext.build().from(code.getContext()));
    }

    /**
     * Basic constructor with the bare error code.
     *
     * @param code the error code that holds all the .
     */
    public TDPException(ErrorCode code) {
        super(code, null, null);
    }


    public boolean isError() {
        return error;
    }
}
