package org.talend.dataprep.metrics.configuration;

import org.springframework.boot.actuate.metrics.repository.InMemoryMetricRepository;
import org.springframework.boot.actuate.metrics.repository.MetricRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.talend.dataprep.metrics.Aspects;

@Configuration
@EnableAspectJAutoProxy
public class MetricsConfiguration {

    @Bean
    Aspects getAspects() {
        return new Aspects();
    }

    @Bean
    MetricRepository getRepository() {
        return new InMemoryMetricRepository();
    }
}
