package org.talend.dataprep.metrics.configuration;

import java.lang.annotation.*;

import org.springframework.context.annotation.Import;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(MetricsConfiguration.class)
public @interface EnableMonitoring {
}
