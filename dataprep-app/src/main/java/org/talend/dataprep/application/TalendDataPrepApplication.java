package org.talend.dataprep.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.ConfigurableEnvironment;

@SpringBootApplication
@Profile("bundled")
@ComponentScan(basePackages = "org.talend.dataprep")
public class TalendDataPrepApplication {

    public static final Logger LOGGER = LoggerFactory.getLogger(TalendDataPrepApplication.class);

    public static void main(String[] args) {
        // start all in one application
        final SpringApplication application = new SpringApplication(TalendDataPrepApplication.class);
        final ConfigurableApplicationContext context = application.run(args);
        final ConfigurableEnvironment environment = context.getEnvironment();
        // log information about current configuration
        LOGGER.info("Metadata location: {}.", environment.getProperty("dataset.metadata.store.file.location"));
        LOGGER.info("Content location: {}.", environment.getProperty("dataset.content.store.file.location"));
        LOGGER.info("Store location: {}.", environment.getProperty("preparation.store.file.location"));
        LOGGER.info("User data location: {}.", environment.getProperty("user.data.store.file.location"));
    }

}
