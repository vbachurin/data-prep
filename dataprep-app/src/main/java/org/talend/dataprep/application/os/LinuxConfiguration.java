package org.talend.dataprep.application.os;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@Conditional(LinuxCondition.class)
public class LinuxConfiguration implements InitializingBean, DisposableBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        // Nothing to do (initialization)
    }

    @Override
    public void destroy() throws Exception {
        // Nothing to do (application shutdown)
    }
}
