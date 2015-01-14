package org.talend.dataprep.metrics;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@org.springframework.context.annotation.Configuration
@ComponentScan(basePackages = "org.talend.dataprep.metrics")
public class Configuration {

    @Bean
    public TimeMeasured getTimeMeasured() {
        return new TimeMeasured();
    }

    @Bean
    public VolumeMeasured getVolumeMeasured() { return new VolumeMeasured();}
}
