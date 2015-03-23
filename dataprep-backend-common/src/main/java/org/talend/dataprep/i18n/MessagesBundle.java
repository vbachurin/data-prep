// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprep.i18n;

import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

@Component
public class MessagesBundle implements ApplicationContextAware {

    private static final Log LOG = LogFactory.getLog(MessagesBundle.class);

    private static ResourceBundleMessageSource source;

    private MessagesBundle() {
    }

    public static String getString(String code, Locale locale) {
        return getString(code, locale, new String[0]);
    }

    public static String getString(String code, Locale locale, Object... args) {
        return source.getMessage(code, args, locale);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        source = applicationContext.getBean(ResourceBundleMessageSource.class);
        LOG.info("Activated i18n messages (" + source + ").");
    }
}
