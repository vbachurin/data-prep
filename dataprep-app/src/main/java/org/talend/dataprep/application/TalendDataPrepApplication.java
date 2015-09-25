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
        // log information about current configuration
        final ConfigurableEnvironment environment = context.getEnvironment();
        LOGGER.info("Connection information:");
        LOGGER.info("Dataset location: {}.", environment.getProperty("dataset.service.url"));
        LOGGER.info("Transformation location: {}.", environment.getProperty("transformation.service.url"));
        LOGGER.info("Preparation location: {}.", environment.getProperty("preparation.service.url"));

        LOGGER.info("Store locations:");
        LOGGER.info("Metadata location: {}.", environment.getProperty("dataset.metadata.store.file.location"));
        LOGGER.info("Content location: {}.", environment.getProperty("dataset.content.store.file.location"));
        LOGGER.info("Store location: {}.", environment.getProperty("preparation.store.file.location"));
        LOGGER.info("User data location: {}.", environment.getProperty("user.data.store.file.location"));
    }

}
