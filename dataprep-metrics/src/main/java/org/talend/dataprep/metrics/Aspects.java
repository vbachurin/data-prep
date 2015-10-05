package org.talend.dataprep.metrics;

import java.io.InputStream;

import javax.servlet.http.Part;

import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.repository.MetricRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

@Aspect
public class Aspects {

    private static final Logger LOGGER = LoggerFactory.getLogger(Aspects.class);

    @Autowired
    MetricRepository repository;

    static String getCategory(Class<?> clazz, String method) throws NoSuchMethodException {
        return clazz.getName() + '.' + method;
    }

    private static UserMetric<Long> buildTimeMetric(ProceedingJoinPoint pjp, long time) throws NoSuchMethodException {
        Signature signature = pjp.getSignature();
        String category = getCategory(signature.getDeclaringType(), signature.getName());
        UserMetric<Long> metric = new UserMetric<>(category + ".time", time);
        return userMetric(metric);
    }

    private static UserMetric<Long> buildVolumeMetric(ProceedingJoinPoint pjp, long volume) throws NoSuchMethodException {
        Signature signature = pjp.getSignature();
        String category = getCategory(signature.getDeclaringType(), signature.getName());
        UserMetric<Long> metric = new UserMetric<>(category + ".volume", volume);
        return userMetric(metric);
    }

    public static <T extends Number> UserMetric<T> userMetric(UserMetric<T> metric) {
        // Get authentication information
        String userName;
        String remoteAddress;
        String sessionId;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Unauthenticated operation access call stack.", new RuntimeException(StringUtils.EMPTY));
            } else {
                LOGGER.error("Unauthenticated operation access (enable DEBUG for detailed error).");
            }
            userName = "N/A";
            remoteAddress = "N/A";
            sessionId = "N/A";
        } else {
            Object principal = authentication.getPrincipal();
            Object details = authentication.getDetails();
            // Get user name
            if (principal instanceof User) {
                userName = ((User) principal).getUsername();
                LOGGER.debug("Authentication: {}", authentication);

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
        // Set information in user metric
        metric.setUser(userName);
        metric.setOrganization(StringUtils.EMPTY);
        metric.setRemoteAddress(remoteAddress);
        metric.setSessionId(sessionId);
        return metric;
    }

    @Around("execution(* *(..)) && @annotation(timed)")
    public Object timed(ProceedingJoinPoint pjp, Timed timed) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            return pjp.proceed(pjp.getArgs());
        } finally {
            long time = System.currentTimeMillis() - start;
            repository.set(buildTimeMetric(pjp, time));
        }
    }

    @Around("execution(* *(..)) && @annotation(volumeMetered)")
    public Object volumeMetered(ProceedingJoinPoint pjp, VolumeMetered volumeMetered) throws Throwable {
        // Find first InputStream available in arguments
        Object[] wrappedArgs = new Object[pjp.getArgs().length];
        final Object[] args = pjp.getArgs();
        for (int i = 0; i < pjp.getArgs().length; i++) {
            Object o = args[i];
            if (o instanceof InputStream) {
                wrappedArgs[i] = new MeteredInputStream((InputStream) o);
            } else if (o instanceof Part) {
                final Part delegate = (Part) o;
                wrappedArgs[i] = new PartWrapper(delegate);
            } else {
                wrappedArgs[i] = o;
            }
        }
        try {
            return pjp.proceed(wrappedArgs);
        } finally {
            for (Object wrappedArg : wrappedArgs) {
                if (wrappedArg instanceof Metered) {
                    repository.set(buildVolumeMetric(pjp, ((Metered) wrappedArg).getVolume()));
                }
            }
        }
    }

}
