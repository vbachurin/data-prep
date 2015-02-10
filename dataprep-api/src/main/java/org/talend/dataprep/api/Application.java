package org.talend.dataprep.api;

import com.netflix.hystrix.Hystrix;
import com.netflix.hystrix.HystrixCommandProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@ComponentScan(basePackages = "org.talend.dataprep")
@EnableAutoConfiguration
public class Application implements DisposableBean {

    private static final Log LOG = LogFactory.getLog(Application.class);

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
