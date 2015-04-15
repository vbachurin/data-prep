package org.talend.dataprep.exception;

import java.util.Arrays;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

@Component
public class Exceptions implements ApplicationContextAware {

    public static final Logger LOGGER = LoggerFactory.getLogger(Exceptions.class);

    @Autowired
    private static ResourceBundleMessageSource bundle;

    private Exceptions() {
    }

    private static TDPException getTdpException(Messages message, Object[] args, Class<? extends TDPException> exceptionClass) {
        Throwable cause = null;
        try {
            Object[] filteredArgs;
            if (args.length > 0 && args[args.length - 1] instanceof Throwable) {
                cause = (Throwable) args[args.length - 1];
                filteredArgs = Arrays.copyOf(args, args.length - 1);
            } else {
                cause = null;
                filteredArgs = args;
            }
            if (cause instanceof UserException) {
                return (TDPException) cause;
            }
            Locale locale = LocaleContextHolder.getLocale();
            final String exceptionMessage = bundle.getMessage(message.getCode(), filteredArgs, locale);
            return exceptionClass.getConstructor(Messages.class, String.class, Throwable.class).newInstance(message,
                    exceptionMessage, cause);
        } catch (Exception e) {
            if (cause != null) {
                LOGGER.error("Unable to send exception user, logging it.", cause);
            }
            throw new InternalException(CommonMessages.UNEXPECTED_EXCEPTION, "Unable to create exception for user.", e);
        }
    }

    public static TDPException Internal(Messages message, Object... args) {
        return getTdpException(message, args, InternalException.class);
    }

    public static TDPException User(Messages message, Object... args) {
        return getTdpException(message, args, UserException.class);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        bundle = applicationContext.getBean(ResourceBundleMessageSource.class);
    }
}
