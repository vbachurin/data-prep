package org.talend.dataprep.dataset.service;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class ServiceCORSFilter implements Filter {

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;
        if (request.getServletPath().startsWith("/datasets")) { //$NON-NLS-1
            response.setHeader("Access-Control-Allow-Origin", "*"); //$NON-NLS-1 //$NON-NLS-2
            response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE"); //$NON-NLS-1 //$NON-NLS-2
            response.setHeader("Access-Control-Max-Age", "3600"); //$NON-NLS-1 //$NON-NLS-2
            response.setHeader("Access-Control-Allow-Headers", "x-requested-with"); //$NON-NLS-1 //$NON-NLS-2
        }
        chain.doFilter(req, res);
    }

    public void init(FilterConfig filterConfig) {
    }

    public void destroy() {
    }
}
