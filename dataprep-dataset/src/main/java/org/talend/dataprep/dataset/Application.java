package org.talend.dataprep.dataset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

@SpringBootApplication
@Profile("standalone")
@EnableMongoRepositories(basePackages = "org.talend.dataprep")
@ComponentScan(basePackages = "org.talend.dataprep")
public class Application implements DisposableBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    @Autowired(required = false)
    private HazelcastInstance hazelcastInstance;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void destroy() throws Exception {
        // Hazelcast shutdown
        if (hazelcastInstance != null) {
            LOGGER.info("Stopping Hazelcast...");
            hazelcastInstance.shutdown();
            Hazelcast.shutdownAll();
            LOGGER.info("Stopped Hazelcast.");
        } else {
            LOGGER.info("No Hazelcast to stop.");
        }

    }
}
