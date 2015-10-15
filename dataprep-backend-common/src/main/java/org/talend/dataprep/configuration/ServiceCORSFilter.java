package org.talend.dataprep.configuration;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

// TODO This class should be replaced with Spring CORS configuration (coming in Spring 4.2, though Spring Boot 1.3)
@Component
public class ServiceCORSFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceCORSFilter.class);

    private final Set<String> serviceRootPaths = new HashSet<>();

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;
        serviceRootPaths.stream().filter(sharedPath -> request.getServletPath().startsWith(sharedPath)).forEach(sharedPath -> {
            response.setHeader("Access-Control-Allow-Origin", "*"); // $NON-NLS-1 //$NON-NLS-2$
            response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT"); // $NON-NLS-1 //$NON-NLS-2$
            response.setHeader("Access-Control-Max-Age", "3600"); // $NON-NLS-1 //$NON-NLS-2$
            response.setHeader("Access-Control-Allow-Headers", "x-requested-with, Content-Type"); // $NON-NLS-1 //$NON-NLS-2$
        });
        chain.doFilter(req, res);
    }

    @Override
    public void init(FilterConfig filterConfig) {
        // nothing to do here
    }

    @Override
    public void destroy() {
        // nothing to do here
    }

    @Autowired(required = false)
    public void initPaths(RequestMappingHandlerMapping handlerMapping) {
        if (handlerMapping == null) {
            return;
        }
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMapping.getHandlerMethods().entrySet()) {
            final Set<String> patterns = entry.getKey().getPatternsCondition().getPatterns();
            updateRootPath(patterns.toArray(new String[patterns.size()]));
        }
        // Log service paths
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Enable Cross Origin settings for paths: {}", Arrays.toString(serviceRootPaths.toArray()));
        }
    }

    private void updateRootPath(String[] urlMappings) {
        for (String urlMapping : urlMappings) {
            serviceRootPaths.add("/" + StringUtils.substringBefore(urlMapping.substring(1), "/")); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

}
