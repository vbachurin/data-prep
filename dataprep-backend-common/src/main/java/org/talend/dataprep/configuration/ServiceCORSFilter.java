package org.talend.dataprep.configuration;

import java.io.IOException;
import java.lang.reflect.Method;
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
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Component
public class ServiceCORSFilter implements Filter, ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger( ServiceCORSFilter.class );

    private final Set<String> serviceRootPaths = new HashSet<>();

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;
        serviceRootPaths.stream().filter(sharedPath -> request.getServletPath().startsWith(sharedPath)).forEach(sharedPath -> {
            response.setHeader("Access-Control-Allow-Origin", "*"); //$NON-NLS-1 //$NON-NLS-2$
                response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT"); //$NON-NLS-1 //$NON-NLS-2$
                response.setHeader("Access-Control-Max-Age", "3600"); //$NON-NLS-1 //$NON-NLS-2$
                response.setHeader("Access-Control-Allow-Headers", "x-requested-with, Content-Type"); //$NON-NLS-1 //$NON-NLS-2$
            });
        chain.doFilter(req, res);
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        // REST Services need special headers for communication with web UI, find REST paths in class definition
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(RestController.class);
        for (Map.Entry<String, Object> currentBean : beans.entrySet()) {
            // Bean might be a proxy, use AopProxyUtils to get actual class definition.
            Method[] methods = AopProxyUtils.ultimateTargetClass(currentBean.getValue()).getMethods();
            for (Method method : methods) {
                RequestMapping annotation = method.getAnnotation(RequestMapping.class);
                if (annotation != null) {
                    String[] urlMappings = annotation.value();
                    for (String urlMapping : urlMappings) {
                        serviceRootPaths.add("/" + StringUtils.substringBefore(urlMapping.substring(1), "/")); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                }
            }
        }
        // Log service paths
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Enable Cross Origin settings for paths: {}", Arrays.toString(serviceRootPaths.toArray()));
        }
    }
}
