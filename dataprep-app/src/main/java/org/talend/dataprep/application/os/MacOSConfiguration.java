package org.talend.dataprep.application.os;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@Conditional(MacOSCondition.class)
public class MacOSConfiguration implements InitializingBean, DisposableBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        // tell macosx to keep the menu associated with the screen and what the app title is
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.eawt.CocoaComponent.CompatibilityMode", "false");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Talend Data Preparation");
    }

    @Override
    public void destroy() throws Exception {
        // Nothing to do (application shutdown)
    }
}
