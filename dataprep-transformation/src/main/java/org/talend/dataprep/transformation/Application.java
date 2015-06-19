package org.talend.dataprep.transformation;

import org.apache.spark.SparkContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "org.talend.dataprep")
public class Application implements DisposableBean {

    public static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

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
    }

}
