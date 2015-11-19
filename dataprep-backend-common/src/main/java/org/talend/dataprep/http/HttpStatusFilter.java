package org.talend.dataprep.http;

import java.io.IOException;

import javax.servlet.*;

import org.springframework.stereotype.Component;

@Component
public class HttpStatusFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Nothing to do
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            HttpContextHolder.response(response);
            chain.doFilter(request, response);
        } finally {
            // Don't forget to clean up
            HttpContextHolder.clear();
        }
    }

    @Override
    public void destroy() {
        // Nothing to do
    }
}
