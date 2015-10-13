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
@Conditional(MacOSCondition.class)
public class MacOSConfiguration implements ApplicationListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(MacOSConfiguration.class);

    @Value("${server.port}")
    private int serverPort;

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof EmbeddedServletContainerInitializedEvent) {
            try {
                // tell macosx to keep the menu associated with the screen and what the app title is
                LOGGER.info( "java.awt.headless false" );
                System.setProperty("java.awt.headless", "false");
                System.setProperty("apple.laf.useScreenMenuBar", "true");
                System.setProperty("com.apple.eawt.CocoaComponent.CompatibilityMode", "false");
                System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Talend Data Preparation");

                final String url = "http://127.0.0.1:" + serverPort + "/ui/index.html";
                Desktop.getDesktop().browse(new URI(url));
            } catch (Exception e) {
                LOGGER.error("Unable to launch web browser.", e);
            }
        }
    }
}
