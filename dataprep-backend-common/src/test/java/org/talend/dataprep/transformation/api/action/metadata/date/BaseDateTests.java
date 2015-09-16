package org.talend.dataprep.transformation.api.action.metadata.date;

import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Base class for all date related unit tests.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = BaseDateTests.class)
@Configuration
@ComponentScan(basePackages = "org.talend.dataprep")
public abstract class BaseDateTests {
}
