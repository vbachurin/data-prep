package org.talend.dataprep.exception;

import java.io.StringWriter;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class TDPExceptionController {

    @ExceptionHandler(InternalException.class)
    public @ResponseBody String handleInternalError(HttpServletResponse response, InternalException e) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        final StringWriter message = new StringWriter();
        e.writeTo(message);
        return message.toString();
    }

    @ExceptionHandler(UserException.class)
    public @ResponseBody String handleUserError(HttpServletResponse response, UserException e) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        final StringWriter message = new StringWriter();
        e.writeTo(message);
        return message.toString();
    }

}
