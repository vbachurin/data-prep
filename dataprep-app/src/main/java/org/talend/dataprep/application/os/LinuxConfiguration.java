package org.talend.dataprep.application.os;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.mock.env.MockPropertySource;

@Configuration
@Conditional(LinuxCondition.class)
public class LinuxConfiguration implements ApplicationListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinuxConfiguration.class);

    @Value("${server.port}")
    private int serverPort;

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationEnvironmentPreparedEvent) {
            final ConfigurableEnvironment environment = ((ApplicationEnvironmentPreparedEvent) event).getEnvironment();
            MockPropertySource properties = new MockPropertySource()
                    .withProperty("dataset.metadata.store.file.location", "/tmp/talend/dataprep/store/datasets/metadata/");
            environment.getPropertySources().addFirst(properties);
        } else if (event instanceof EmbeddedServletContainerInitializedEvent) {
            try {
                final String url = "http://127.0.0.1:" + serverPort + "/ui/index.html";
                Runtime.getRuntime().exec("xdg-open " + url);
            } catch (IOException e) {
                LOGGER.error("Unable to launch web browser.", e);
            }
        }
    }
}
