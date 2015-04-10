package org.talend.dataprep.exception;

import java.io.IOException;
import java.io.Writer;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

class TDPException extends RuntimeException {

    private final Messages code;

    private final String message;

    private Throwable cause;

    public TDPException(Messages code, String message) {
        this.code = code;
        this.message = message;
    }

    public TDPException(Messages code, String message, Throwable cause) {
        this.code = code;
        this.message = message;
        this.cause = cause;
    }

    public void writeTo(Writer writer) {
        try {
            JsonGenerator generator = (new JsonFactory()).createGenerator(writer);
            generator.writeStartObject();
            {
                generator.writeStringField("code", code.getProduct() + '_' + code.getGroup() + '_' + code.getCode());
                generator.writeStringField("message", message);
                generator.writeStringField("cause", cause.getMessage());
            }
            generator.writeEndObject();
            generator.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
