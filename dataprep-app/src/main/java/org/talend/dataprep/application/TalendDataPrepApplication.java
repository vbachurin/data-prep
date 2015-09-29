package org.talend.dataprep.application;

import java.io.IOException;
import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.security.util.InMemoryResource;
import org.talend.dataprep.application.configuration.APIConfig;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
@Profile("bundled")
@ComponentScan(basePackages = "org.talend.dataprep")
public class TalendDataPrepApplication {

    public static final Logger LOGGER = LoggerFactory.getLogger(TalendDataPrepApplication.class);

    public static void main(String[] args) {
        // start all in one application
        final SpringApplication application = new SpringApplication(TalendDataPrepApplication.class);
        application.setResourceLoader(new DefaultResourceLoader() {

            @Override
            public Resource getResource(String location) {
                if ("classpath:/META-INF/resources/dist/assets/config/config.json".equals(location)) {
                    try {
                        // Build custom configuration
                        final APIConfig config = new APIConfig();
                        config.setServerUrl("http://127.0.0.1:" + "8080" +"/");
                        final StringWriter writer = new StringWriter();
                        final ObjectMapper objectMapper = new ObjectMapper();
                        objectMapper.writer().writeValue(writer, config);
                        // Return a resource for this JSON configuration
                        LOGGER.debug("Web UI configuration content: {}.", writer);
                        return new InMemoryResource(writer.toString());
                    } catch (IOException e) {
                        LOGGER.error("Unable to provide Web UI configuration.", e);
                        return super.getResource(location);
                    }
                } else {
                    return super.getResource(location);
                }
            }
        });
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
