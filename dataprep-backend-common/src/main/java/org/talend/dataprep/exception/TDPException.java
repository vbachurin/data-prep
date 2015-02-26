package org.talend.dataprep.exception;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;
import java.io.Writer;

class TDPException extends RuntimeException {

    private final Messages message;

    private Throwable cause;

    public TDPException(Messages message) {
        this.message = message;
    }

    public TDPException(Messages message, Throwable cause) {
        this.message = message;
        this.cause = cause;
    }

    public void writeTo(Writer writer) {
        try {
            JsonGenerator generator = (new JsonFactory()).createGenerator(writer);
            generator.writeStartObject();
            {
                generator.writeStringField("code", message.getProduct() + '_' + message.getGroup() + '_' + message.getCode());
                generator.writeStringField("cause", cause.getMessage());
            }
            generator.writeEndObject();
            generator.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
