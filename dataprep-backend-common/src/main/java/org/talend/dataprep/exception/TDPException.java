package org.talend.dataprep.exception;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Root class for all TDP exceptions.
 */
public class TDPException extends RuntimeException {

    /** This class' logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(TDPException.class);


    /** The error code for this exception. */
    private final ErrorCode code;
    /** The exception cause. */
    private Throwable cause;
    /** Context of the error when it occurred (used to detail the user error message in frontend). */
    private TDPExceptionContext context;


    /**
     * Full constructor.
     *
     * @param code the error code that holds all the .
     * @param cause the root cause of this error.
     * @param context the context of the error when it occurred (used to detail the user error message in frontend).
     */
    public TDPException(ErrorCode code, Throwable cause, TDPExceptionContext context) {
        super(code.getCode(), cause);
        this.code = code;
        this.cause = cause;
        this.context = context;

        //TODO Vince check if context match the expected one from the error code
    }

    /**
     * Light constructor without context.
     *
     * @param code the error code that holds all the .
     * @param cause the root cause of this error.
     */
    public TDPException(ErrorCode code, Throwable cause) {
        this(code, cause, null);
    }


    /**
     * Basic contstructor with the bare error code.
     *
     * @param code the error code that holds all the .
     */
    public TDPException(ErrorCode code) {
        this(code, null, null);
    }


    /**
     * @return the error code.
     */
    public ErrorCode getCode() {
        return code;
    }


    /**
     * Describe this error in json into the given writer.
     * @param writer where to write this error.
     */
    public void writeTo(Writer writer) {
        try {
            JsonGenerator generator = (new JsonFactory()).createGenerator(writer);
            generator.writeStartObject();
            {
                generator.writeStringField("code", code.getProduct() + '_' + code.getGroup() + '_' + code.getCode()); //$NON-NLS-1$
                generator.writeStringField("message", code.getCode()); //$NON-NLS-1$
                if (cause != null) {
                    generator.writeStringField("cause", cause.getMessage()); //$NON-NLS-1$
                }
                if (context != null) {
                    for (Map.Entry<String, Object> entry : context.entries()) {
                        generator.writeStringField(entry.getKey(), entry.getValue().toString());
                    }
                }
            }
            generator.writeEndObject();
            generator.flush();
        } catch (IOException e) {
            LOGGER.error("Unable to write exception to " + writer + ".", e);
        }
    }
}
