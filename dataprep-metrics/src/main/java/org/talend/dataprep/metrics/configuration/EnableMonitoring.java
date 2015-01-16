package org.talend.dataprep.metrics.configuration;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(MetricsConfiguration.class)
public @interface EnableMonitoring {
}
