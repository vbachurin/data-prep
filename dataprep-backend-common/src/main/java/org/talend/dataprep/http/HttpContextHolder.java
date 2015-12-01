package org.talend.dataprep.http;

import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * A helper class around {@link RequestContextHolder}: allow simple modifications on HTTP context without worrying
 * whether code is called in a web context or not.
 */
public class HttpContextHolder {

    public static void status(HttpStatus status) {
        final RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes != null && attributes instanceof ServletRequestAttributes) {
            ((ServletRequestAttributes) attributes).getResponse().setStatus(status.value());
        }
    }

    public static void header(String header, String value) {
        final RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes != null && attributes instanceof ServletRequestAttributes) {
            ((ServletRequestAttributes) attributes).getResponse().setHeader(header, value);
        }
    }

}
