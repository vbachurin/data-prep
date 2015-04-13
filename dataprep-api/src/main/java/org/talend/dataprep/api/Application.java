package org.talend.dataprep.api;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.netflix.hystrix.Hystrix;

@SpringBootApplication
@ComponentScan(basePackages = "org.talend.dataprep")
public class Application implements DisposableBean {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void destroy() throws Exception {
        LOG.info("Shutting down Hystrix...");
        // shutdown all thread pools; waiting a little time for shutdown
        Hystrix.reset(1, TimeUnit.SECONDS);
        LOG.info("Shut down Hystrix done.");
    }
}
