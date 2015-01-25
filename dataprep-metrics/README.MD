# Talend Data Preparation - Service Usage Metrics
![alt text](http://www.talend.com/sites/all/themes/talend_responsive/images/logo.png "Talend")

This folder contains a library to monitor REST services usage. It monitors:
* User information
* Volume information (e.g. the amount of data exchanged between client and server).

## Prerequisites

You need Java *8* (or higher) and Maven 3.x (tested with 3.2.2 on Fedora 21 with OpenJDK 1.8.0_25-b18).

## Usage
Simply add the Maven dependency to the project you'd like to use metrics:
```
<dependency>
    <groupId>org.talend.dataprep</groupId>
    <artifactId>dataprep-metrics</artifactId>
    <version>1.0.M0-SNAPSHOT</version>
</dependency>
```
In your application, add the @EnableMonitoring annotation to a Spring @Configuration class. such as:
```
package org.talend.dataprep.dataset.configuration;

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
```
The configuration class must provide a org.springframework.boot.actuate.metrics.writer.MetricWriter and
a org.springframework.boot.actuate.metrics.reader.MetricReader instance (or a org.springframework.boot.actuate.metrics.repository.MetricRepository,
since it implements both).

You may look to unit test classes of this project for usage examples.

## License

Copyright (c) 2006-2015 Talend
