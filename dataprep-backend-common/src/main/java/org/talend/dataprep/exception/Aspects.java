package org.talend.dataprep.exception;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;

@Configuration
@Aspect
class Aspects {

    private static final Logger LOG = LoggerFactory.getLogger(Aspects.class);

    @Around("execution(* *(..)) && @annotation(requestMapping)")
    public Object exception(ProceedingJoinPoint pjp, RequestMapping requestMapping) throws Throwable {
        try {
            return pjp.proceed(pjp.getArgs());
        } catch (TDPException e) {
            throw e;
        } catch (Exception e) {
            LOG.error("Exception occurred in '" + pjp.getSignature().toShortString() + "'", e);
            throw Exceptions.Internal(DefaultMessage.UNEXPECTED_EXCEPTION, e);
        }
    }

    enum DefaultMessage implements Messages {
        UNEXPECTED_EXCEPTION;

        @Override
        public String getProduct() {
            return "TDP";
        }

        @Override
        public String getGroup() {
            return "ALL";
        }
    }

}
