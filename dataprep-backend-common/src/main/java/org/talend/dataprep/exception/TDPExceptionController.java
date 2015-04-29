package org.talend.dataprep.exception;

import java.io.StringWriter;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller advice applied to all controller so that they can handle TDPExceptions.
 */
@ControllerAdvice
public class TDPExceptionController {

    /** This class' logger. */
    protected static final Logger LOGGER = LoggerFactory.getLogger(TDPExceptionController.class);

    /**
     * Send the TDPException into the http response.
     *
     * @param response the http response where to write the error.
     * @param e the TDP exception.
     * @return the http response.
     */
    @ExceptionHandler(TDPException.class)
    public @ResponseBody String handleError(HttpServletResponse response, TDPException e) {

        LOGGER.error("unhandled exception", e);

        response.setStatus(e.getCode().getHttpStatus());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        final StringWriter message = new StringWriter();
        e.writeTo(message);
        return message.toString();
    }

}
