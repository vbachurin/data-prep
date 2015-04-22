package org.talend.dataprep.exception;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(Exceptions.class);

    private static final String PACKAGE_PREFIX = "org.talend"; //$NON-NLS-1$

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
            String exceptionMessage = StringUtils.EMPTY;
            if (bundle != null) {
                exceptionMessage = bundle.getMessage(message.getCode(), filteredArgs, locale);
            }
            final Constructor<? extends TDPException> constructor = exceptionClass.getConstructor(Messages.class, String.class, Throwable.class);
            final TDPException tdpException = constructor.newInstance(message, exceptionMessage, cause);
            cleanStackTrace(tdpException); // Remove additional lines in stack trace
            return tdpException;
        } catch (TDPException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Unable to send exception user, logging it.", cause != null ? cause : e);
            throw new InternalException(CommonMessages.UNEXPECTED_EXCEPTION, "Unable to create exception for user.", e);
        }
    }

    /**
     * Removes all stack trace elements added by processing in this class.
     * @param tdpException The {@link TDPException exception} to be cleaned up.
     */
    private static void cleanStackTrace(TDPException tdpException) {
        final StackTraceElement[] elements = tdpException.getStackTrace();
        StackTraceElement[] cleanedElements = elements;
        int i = 0;
        for (StackTraceElement element : elements) {
            final String className = element.getClassName();
            if (className.startsWith(PACKAGE_PREFIX) && !Exceptions.class.getName().equals(className)) {
                cleanedElements = Arrays.copyOfRange(elements, i, elements.length);
                break;
            }
            i++;
        }
        tdpException.setStackTrace(cleanedElements);
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
