package org.talend.dataprep.exception;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;

import com.netflix.hystrix.exception.HystrixRuntimeException;

@Configuration
@Aspect
class Aspects {

    private static final Logger LOG = LoggerFactory.getLogger(Aspects.class);

    @Around("execution(* *(..)) && @annotation(requestMapping)")
    public Object exception(ProceedingJoinPoint pjp, RequestMapping requestMapping) throws Throwable {
        try {
            return pjp.proceed(pjp.getArgs());
        }
        catch (TDPException e) {
            throw e; // Let TDPException pass through (to be processed in correct HTTP code by controller advice).
        }
        // filter out hystrix exception level if possible
        catch (HystrixRuntimeException hre) {
            if (hre.getCause() instanceof TDPException) {
                throw hre.getCause();
            }
            throw hre;
        }
        catch (Exception e) {
            LOG.error("Unexpected exception occurred in '" + pjp.getSignature().toShortString() + "'", e);
            throw new TDPException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
        }
    }

}
