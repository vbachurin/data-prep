package org.talend.dataprep.exception;

import java.io.StringWriter;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class TDPExceptionController {

    private static HttpEntity<String> buildHttpEntity(TDPException e) {
        StringWriter writer = new StringWriter();
        e.writeTo(writer);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(writer.toString(), headers);
    }

    @ExceptionHandler(InternalException.class)
    public HttpEntity<String> handleInternalError(HttpServletResponse response, InternalException e) {
        HttpEntity<String> entity = buildHttpEntity(e);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return entity;
    }

    @ExceptionHandler(UserException.class)
    public HttpEntity<String> handleUserError(HttpServletResponse response, UserException e) {
        HttpEntity<String> entity = buildHttpEntity(e);
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return entity;
    }

}
