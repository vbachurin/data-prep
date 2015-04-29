package org.talend.dataprep.exception;

import java.io.StringWriter;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class TDPExceptionController {

    @ExceptionHandler(TDPException.class)
    public @ResponseBody String handleInternalError(HttpServletResponse response, TDPException e) {
        response.setStatus(e.getCode().getHttpStatus());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        final StringWriter message = new StringWriter();
        e.writeTo(message);
        return message.toString();
    }


}
