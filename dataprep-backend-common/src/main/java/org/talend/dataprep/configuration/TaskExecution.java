package org.talend.dataprep.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.talend.dataprep.schema.csv.CSVSerializer;

@Configuration
public class TaskExecution {

    /**
     * @return A {@link TaskExecutor} for non-blocking CSV serialization.
     * @see CSVSerializer
     */
    @Bean(name = "serializer#csv#executor")
    TaskExecutor getTaskExecutor() {
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(10);
        executor.setWaitForTasksToCompleteOnShutdown(false);
        return executor;
    }
}
