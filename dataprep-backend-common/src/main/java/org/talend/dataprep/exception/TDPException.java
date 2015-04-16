package org.talend.dataprep.exception;

import java.io.IOException;
import java.io.Writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

class TDPException extends RuntimeException {

    private static final Logger LOGGER = LoggerFactory.getLogger(TDPException.class);

    private final Messages code;

    private final String message;

    private Throwable cause;

    public TDPException(Messages code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
        this.cause = cause;
    }

    public void writeTo(Writer writer) {
        try {
            JsonGenerator generator = (new JsonFactory()).createGenerator(writer);
            generator.writeStartObject();
            {
                generator.writeStringField("code", code.getProduct() + '_' + code.getGroup() + '_' + code.getCode()); //$NON-NLS-1$
                generator.writeStringField("message", message); //$NON-NLS-1$
                if (cause != null) {
                    generator.writeStringField("cause", cause.getMessage()); //$NON-NLS-1$
                }
            }
            generator.writeEndObject();
            generator.flush();
        } catch (IOException e) {
            LOGGER.error("Unable to write exception to " + writer + ".", e);
        }
    }
}
