package org.talend.dataprep.exception;

import java.io.StringWriter;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class TDPExceptionController {

    public static final Logger LOGGER = LoggerFactory.getLogger(TDPExceptionController.class);

    @ExceptionHandler(InternalException.class)
    public @ResponseBody String handleInternalError(HttpServletResponse response, InternalException e) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        final StringWriter message = new StringWriter();
        e.writeTo(message);
        return message.toString();
    }

    @ExceptionHandler(UserException.class)
    public @ResponseBody String handleUserError(HttpServletResponse response, UserException e) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        final StringWriter message = new StringWriter();
        e.writeTo(message);
        return message.toString();
    }

}
