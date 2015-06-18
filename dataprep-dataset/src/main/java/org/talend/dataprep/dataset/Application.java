package org.talend.dataprep.dataset;

import org.apache.spark.SparkContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

@SpringBootApplication
@ComponentScan(basePackages = "org.talend.dataprep")
public class Application implements DisposableBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    @Autowired(required = false)
    private HazelcastInstance hazelcastInstance;

    @Autowired(required = false)
    private SparkContext context;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void destroy() throws Exception {
        // Spark shutdown
        if (context != null) {
            LOGGER.info("Stopping Spark context...");
            context.stop();
            LOGGER.info("Spark context stopped.");
        } else {
            LOGGER.info("No Spark context to stop.");
        }
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
