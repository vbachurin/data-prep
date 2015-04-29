package org.talend.dataprep.exception;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collections;

@Configuration
@Aspect
class Aspects {

    private static final Logger LOG = LoggerFactory.getLogger(Aspects.class);

    @Around("execution(* *(..)) && @annotation(requestMapping)")
    public Object exception(ProceedingJoinPoint pjp, RequestMapping requestMapping) throws Throwable {
        try {
            return pjp.proceed(pjp.getArgs());
        } catch (TDPException e) {
            throw e; // Let TDPException pass through (to be processed in correct HTTP code by controller advice).
        } catch (Exception e) {
            LOG.error("Unexpected exception occurred in '" + pjp.getSignature().toShortString() + "'", e);
            throw new TDPException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
        }
    }

}
