package org.talend.dataprep.metrics;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.boot.actuate.metrics.repository.MetricRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.io.InputStream;

@Aspect
public class Aspects {

    @Autowired
    MetricRepository repository;

    private static final Log LOGGER = LogFactory.getLog(Aspects.class);

    static String getCategory(Class<?> clazz, String method) throws NoSuchMethodException {
        return clazz.getName() + '.' + method;
    }

    @Around("execution(* *(..)) && @annotation(timed)")
    public Object timed(ProceedingJoinPoint pjp, Timed timed) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            return pjp.proceed(pjp.getArgs());
        } finally {
            long time = System.currentTimeMillis() - start;
            Signature signature = pjp.getSignature();
            String category = getCategory(signature.getDeclaringType(), signature.getName());
            repository.set(new Metric<Number>(category + ".time", time)); //$NON-NLS-1
        }
    }

    @Around("execution(* *(..)) && @annotation(userMetered)")
    public Object userMetered(ProceedingJoinPoint pjp, UserMetered userMetered) throws Throwable {
        // Get authentication information
        String userName;
        String remoteAddress;
        String sessionId;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            LOGGER.error("Unauthenticated operation access (see stack trace for calls).", new RuntimeException());
            userName = "N/A";
            remoteAddress = "N/A";
            sessionId = "N/A";
        } else {
            Object principal = authentication.getPrincipal();
            Object details = authentication.getDetails();
            // Get user name
            if (principal instanceof User) {
                userName = ((User) principal).getUsername();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Authentication: " + authentication);
                }
            } else {
                userName = principal.toString();
            }
            // Get details
            if (details instanceof WebAuthenticationDetails) {
                remoteAddress = ((WebAuthenticationDetails) details).getRemoteAddress();
                sessionId = ((WebAuthenticationDetails) details).getSessionId();
            } else {
                remoteAddress = "N/A";
                sessionId = "N/A";
            }
        }
        return pjp.proceed(pjp.getArgs());
    }

    @Around("execution(* *(..)) && @annotation(volumeMetered)")
    public Object volumeMetered(ProceedingJoinPoint pjp, VolumeMetered volumeMetered) throws Throwable {
        // Find first InputStream available in arguments
        int argumentIndex = -1;
        for (Object o : pjp.getArgs()) {
            argumentIndex++;
            if (InputStream.class.isAssignableFrom(o.getClass())) {
                break;
            }
        }
        if (argumentIndex < 0) {
            LOGGER.warn("Unable to find a valid InputStream to wrap for meter in method '" + pjp.getSignature().toLongString()
                    + "'.");
        }
        // Wraps InputStream (if any)
        Object[] args = pjp.getArgs();
        if (argumentIndex >= 0) {
            MeteredInputStream meteredInputStream = new MeteredInputStream((InputStream) args[argumentIndex]);
            args[argumentIndex] = meteredInputStream;
            Object o = pjp.proceed(args);
            // registry.meter(pjp.getSignature().toLongString() + ".volume").mark(meteredInputStream.getVolume());
            return o;
        } else {
            return pjp.proceed(args);
        }
    }

}
