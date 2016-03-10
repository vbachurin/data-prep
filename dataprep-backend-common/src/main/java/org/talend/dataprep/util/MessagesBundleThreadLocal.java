//  ============================================================================
//
//  Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
//  This source code is available under agreement available at
//  https://github.com/Talend/data-prep/blob/master/LICENSE
//
//  You should have received a copy of the agreement
//  along with this program; if not, write to Talend SA
//  9 rue Pages 92150 Suresnes, France
//
//  ============================================================================

package org.talend.dataprep.util;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.talend.dataprep.i18n.MessagesBundle;

/**
 * This bean and servlet Filer will hold a ThreadLocal variable with a reference to the bean
 * {@link MessagesBundle}
 */
@Component
public class MessagesBundleThreadLocal
    implements Filter {


    @Autowired
    private MessagesBundle messagesBundle;

    private static final ThreadLocal<MessagesBundle> THREAD_LOCAL = new ThreadLocal<MessagesBundle>() {

        protected MessagesBundle initialValue() {
            // we do not have any initial value here
            return null;
        }
    };

    /**
     * will setup the current thread. Can help for startup thread (and ease unit testing as well)
     */
    @PostConstruct
    public void initializeCurrentThread() {
        MessagesBundleThreadLocal.set( messagesBundle);
    }

    public static MessagesBundle get() {
        return THREAD_LOCAL.get();
    }

    public static void set(MessagesBundle messagesBundle) {
        THREAD_LOCAL.set(messagesBundle);
    }

    @Override
    public void destroy() {
        // really to be sure it's clean because doFilter do it as well.
        THREAD_LOCAL.remove();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        THREAD_LOCAL.set( messagesBundle );
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            THREAD_LOCAL.remove();
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // no op
    }
}
