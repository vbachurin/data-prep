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
    /** package prefix to remove while cleaning stacktrace. */
    private static final String PACKAGE_PREFIX = "org.talend"; //$NON-NLS-1$


    /** The error code for this exception. */
    private final ErrorCode code;
    /** The exception cause. */
    private Throwable cause;
    /** Context of the error when it occurred (used to detail the user error message in frontend). */
    private Map<String, Object> context;


    /**
     * Full constructor.
     *
     * @param code the error code that holds all the .
     * @param cause the root cause of this error.
     * @param context the context of the error when it occurred (used to detail the user error message in frontend).
     */
    public TDPException(ErrorCode code, Throwable cause, Map<String, Object> context) {
        super(code.getCode(), cause);
        this.code = code;
        this.cause = cause;
        if (context == null) {
            this.context = Collections.emptyMap();
        }
        else {
            this.context = context;
        }

        cleanStackTrace();
    }

    /**
     * Light constructor without context.
     *
     * @param code the error code that holds all the .
     * @param cause the root cause of this error.
     */
    public TDPException(ErrorCode code, Throwable cause) {
        this(code, cause, Collections.emptyMap());
    }


    /**
     * Basic contstructor with the bare error code.
     *
     * @param code the error code that holds all the .
     */
    public TDPException(ErrorCode code) {
        this(code, null, Collections.emptyMap());
    }


    /**
     * @return the error code.
     */
    public ErrorCode getCode() {
        return code;
    }


    /**
     * @return the error context.
     */
    public Map<String, Object> getContext() {
        return context;
    }

    /**
     * Removes all stack trace elements added by processing in this class.
     */
    private void cleanStackTrace() {
        final StackTraceElement[] elements = this.getStackTrace();
        StackTraceElement[] cleanedElements = elements;
        int i = 0;
        for (StackTraceElement element : elements) {
            final String className = element.getClassName();
            if (className.startsWith(PACKAGE_PREFIX)) {
                cleanedElements = Arrays.copyOfRange(elements, i, elements.length);
                break;
            }
            i++;
        }
        this.setStackTrace(cleanedElements);
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
                ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue(generator, context);
            }
            generator.writeEndObject();
            generator.flush();
        } catch (IOException e) {
            LOGGER.error("Unable to write exception to " + writer + ".", e);
        }
    }
}
