package org.talend.dataprep.configuration;

import org.springframework.core.task.TaskExecutor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Task Executor that replicate current security context
 */
public class AuthenticatedTaskExecutor implements TaskExecutor {

    private final TaskExecutor delegate;

    private AuthenticatedTaskExecutor(TaskExecutor delegate) {
        this.delegate = delegate;
    }

    public static AuthenticatedTaskExecutor authenticated(TaskExecutor executor) {
        return new AuthenticatedTaskExecutor(executor);
    }

    @Override
    public void execute(Runnable task) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        delegate.execute(() -> {
            try {
                final SecurityContext ctx = SecurityContextHolder.createEmptyContext();
                ctx.setAuthentication(authentication);
                SecurityContextHolder.setContext(ctx);
                task.run();
            } finally {
                SecurityContextHolder.clearContext();
            }
        });
    }

}