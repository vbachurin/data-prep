package org.talend.dataprep.preparation.configuration;

import org.springframework.boot.actuate.metrics.repository.InMemoryMetricRepository;
import org.springframework.boot.actuate.metrics.repository.MetricRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.talend.dataprep.metrics.configuration.EnableMonitoring;

@Configuration
@EnableMonitoring
public class Monitoring {

    @Bean
    MetricRepository getRepository() {
        return new InMemoryMetricRepository();
    }
}
