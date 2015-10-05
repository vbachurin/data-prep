package org.talend.dataprep.application.os;

import java.awt.*;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@Conditional(WindowsCondition.class)
public class WindowsConfiguration implements ApplicationListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(WindowsConfiguration.class);

    @Value("${server.port}")
    private int serverPort;

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof EmbeddedServletContainerInitializedEvent) {
            try {
                System.setProperty("java.awt.headless", "false");
                final String url = "http://127.0.0.1:" + serverPort + "/ui/index.html";
                Desktop.getDesktop().browse(new URI(url));

            } catch (Exception e) {
                LOGGER.error("Unable to launch web browser.", e);
            }
        }
    }
}
