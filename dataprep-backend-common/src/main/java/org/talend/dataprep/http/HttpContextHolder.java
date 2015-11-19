package org.talend.dataprep.http;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;

public class HttpContextHolder {

    private static final ThreadLocal<HttpServletResponse> responseHolder = new ThreadLocal<>();

    public static void status(HttpStatus status) {
        final HttpServletResponse response = responseHolder.get();
        if (response != null) {
            response.setStatus(status.value());
        }
    }

    public static void header(String header, String value) {
        final HttpServletResponse response = responseHolder.get();
        if (response != null) {
            response.setHeader(header, value);
        }
    }

    public static void clear() {
        responseHolder.remove();
    }

    public static void response(ServletResponse response) {
        if (response instanceof HttpServletResponse) {
            responseHolder.set((HttpServletResponse) response);
        }
    }
}
