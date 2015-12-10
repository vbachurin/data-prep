package org.talend.dataprep.exception;

import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.talend.dataprep.http.HttpResponseContext;

/**
 * Controller advice applied to all controller so that they can handle TDPExceptions.
 */
@ControllerAdvice
public class TDPExceptionController {

    /** This class' logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(TDPExceptionController.class);

    /**
     * Send the TDPException into the http response.
     *
     * @param e the TDP exception.
     * @return the http response.
     */
    @ExceptionHandler(TDPException.class)
    @ResponseBody
    public String handleError(TDPException e) {

        if (!e.isError()) {
            LOGGER.error( "An error occurred", e );
        }
        HttpResponseContext.status(HttpStatus.valueOf(e.getCode().getHttpStatus()));
        HttpResponseContext.header("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        final StringWriter message = new StringWriter();
        e.writeTo(message);
        return message.toString();
    }

}
