package org.talend.dataprep.configuration;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.util.concurrent.ListenableFuture;
import org.talend.dataprep.i18n.MessagesBundle;
import org.talend.dataprep.util.MessagesBundleContext;

/**
 * Task Executor that replicate current security context
 */
public class MessageBundleTaskExecutor implements AsyncListenableTaskExecutor {

    private final TaskExecutor delegate;

    private MessageBundleTaskExecutor(TaskExecutor delegate) {
        this.delegate = delegate;
    }

    public static AsyncListenableTaskExecutor messageBundle(AsyncListenableTaskExecutor executor) {
        return new MessageBundleTaskExecutor(executor);
    }

    private AsyncListenableTaskExecutor asAsyncListenable() {
        if (!(delegate instanceof AsyncListenableTaskExecutor)) {
            throw new IllegalStateException("Delegate is not a async listenable executor (" + delegate.getClass() + ").");
        }
        return (AsyncListenableTaskExecutor) delegate;
    }

    private static Runnable toRunnable(Runnable runnable, MessagesBundle messagesBundle) {
        return () -> {
            try {
                if (messagesBundle != null) {
                    MessagesBundleContext.set(messagesBundle);
                }
                runnable.run();
            } finally {
                MessagesBundleContext.clear();
            }
        };
    }

    private static <T> Callable<T> toCallable(Callable<T> callable, MessagesBundle messagesBundle) {
        return () -> {
            try {
                if (messagesBundle != null) {
                    MessagesBundleContext.set(messagesBundle);
                }
                return callable.call();
            } finally {
                MessagesBundleContext.clear();
            }
        };
    }

    @Override
    public void execute(Runnable task) {
        final MessagesBundle messagesBundle = MessagesBundleContext.get();
        delegate.execute(toRunnable(task, messagesBundle));
    }

    @Override
    public ListenableFuture<?> submitListenable(Runnable runnable) {
        final MessagesBundle messagesBundle = MessagesBundleContext.get();
        return asAsyncListenable().submitListenable(toRunnable(runnable, messagesBundle));
    }

    @Override
    public <T> ListenableFuture<T> submitListenable(Callable<T> callable) {
        final MessagesBundle messagesBundle = MessagesBundleContext.get();
        return asAsyncListenable().submitListenable(toCallable(callable, messagesBundle));
    }

    @Override
    public void execute(Runnable runnable, long l) {
        final MessagesBundle messagesBundle = MessagesBundleContext.get();
        asAsyncListenable().execute(toRunnable(runnable, messagesBundle), l);
    }

    @Override
    public Future<?> submit(Runnable runnable) {
        final MessagesBundle messagesBundle = MessagesBundleContext.get();
        return asAsyncListenable().submit(toRunnable(runnable, messagesBundle));
    }

    @Override
    public <T> Future<T> submit(Callable<T> callable) {
        final MessagesBundle messagesBundle = MessagesBundleContext.get();
        return asAsyncListenable().submit(toCallable(callable, messagesBundle));
    }
}