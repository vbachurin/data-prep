package org.talend.dataprep.dataset.configuration;

import org.springframework.boot.actuate.metrics.repository.InMemoryMetricRepository;
import org.springframework.boot.actuate.metrics.repository.MetricRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.talend.dataprep.metrics.configuration.EnableMonitoring;

@Configuration(value = "org.talend.dataprep.dataset.configuration.Monitoring")
@EnableMonitoring
public class Monitoring {

    @Bean
    MetricRepository getRepository() {
        return new InMemoryMetricRepository();
    }
}
